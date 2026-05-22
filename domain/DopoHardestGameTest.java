package domain;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.awt.Color;
import java.util.List;

/**
 * Suite de pruebas unitarias — The DOPO Hardest Game (JUnit 4)
 * Cubre los bugs identificados en la rúbrica de revisión.
 */
public class DopoHardestGameTest {

    // ─────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────

    private LevelConfig minimalConfig() {
        LevelConfig c = new LevelConfig();
        c.playerX     = 10;
        c.playerY     = 10;
        c.playerSize  = 20;
        c.playerSpeed = 3;
        c.timeLimit   = 300;
        c.entities.add(new SafeZone(0, 0, 60, 60, SafeZone.Type.INITIAL));
        c.entities.add(new SafeZone(500, 300, 80, 80, SafeZone.Type.FINAL));
        return c;
    }

    private LevelConfig configWithCoin() {
        LevelConfig c = minimalConfig();
        c.entities.add(new Coin(200, 200, 15));
        return c;
    }

    private LevelConfig configWithEnemyAndCoin() {
        LevelConfig c = configWithCoin();
        Enemy e = new Enemy(300, 300, 15, 2);
        e.setMovement(new HorizontalMovement(e, 280, 420, 2));
        c.entities.add(e);
        return c;
    }

    private Board boardSingle(LevelConfig config, String playerType) {
        Board b = new Board(700, 500);
        b.loadFromConfig(config, playerType);
        return b;
    }

    private Board boardSingle(LevelConfig config) {
        return boardSingle(config, "RED");
    }

    // ═════════════════════════════════════════════
    //  MÓDULO 1 — JUEGO BASE
    // ═════════════════════════════════════════════

    @Test
    public void playerMovesRight() {
        Board board = boardSingle(minimalConfig());
        int startX = board.getPlayer().getX();
        board.movePlayer("RIGHT");
        assertTrue("El jugador debe moverse a la derecha",
            board.getPlayer().getX() > startX);
    }

    @Test
    public void playerDoesNotExitLeftBorder() {
        Board board = boardSingle(minimalConfig());
        for (int i = 0; i < 200; i++) board.movePlayer("LEFT");
        assertTrue("El jugador no debe salir por el borde izquierdo",
            board.getPlayer().getX() >= 0);
    }

    @Test
    public void playerDoesNotExitRightBorder() {
        Board board = boardSingle(minimalConfig());
        int boardW  = board.getCols();
        int playerW = board.getPlayer().getWidth();
        for (int i = 0; i < 500; i++) board.movePlayer("RIGHT");
        assertTrue("El jugador no debe salir por el borde derecho",
            board.getPlayer().getX() + playerW <= boardW);
    }

    @Test
    public void playerMovesDiagonalUpRight() {
        LevelConfig c = minimalConfig();
        c.playerY = 200;
        Board board = boardSingle(c);
        int startX = board.getPlayer().getX();
        int startY = board.getPlayer().getY();
        board.movePlayer("UP_RIGHT");
        assertTrue("Debe moverse a la derecha en diagonal",
            board.getPlayer().getX() > startX);
        assertTrue("Debe moverse hacia arriba en diagonal",
            board.getPlayer().getY() < startY);
    }

    @Test
    public void playerDoesNotCrossWall() {
        LevelConfig c = minimalConfig();
        c.entities.add(new Walls(50, 0, 20, 500));
        Board board = boardSingle(c);
        for (int i = 0; i < 100; i++) board.movePlayer("RIGHT");
        assertTrue("El jugador no debe atravesar la pared",
            board.getPlayer().getX() + board.getPlayer().getWidth() <= 50);
    }

    // ── Colisión jugador-enemigo ──

    @Test
    public void enemyCollisionAddsOneDeath() {
        Board board = boardSingle(configWithEnemyAndCoin());
        Player player = board.getPlayer();
        Enemy enemy = board.getEnemies().get(0);
        player.setX(enemy.getX());
        player.setY(enemy.getY());
        board.update();
        assertEquals("Debe registrarse 1 muerte al tocar un enemigo",
            1, board.getTotalDeaths());
    }

    @Test
    public void playerReappearsAtSpawnAfterHit() {
        LevelConfig c = configWithEnemyAndCoin();
        Board board = boardSingle(c);
        Player player = board.getPlayer();
        int spawnX = c.playerX;
        int spawnY = c.playerY;
        Enemy enemy = board.getEnemies().get(0);
        player.setX(enemy.getX());
        player.setY(enemy.getY());
        board.update();
        assertEquals("El jugador debe reaparecer en spawnX", spawnX, player.getX());
        assertEquals("El jugador debe reaparecer en spawnY", spawnY, player.getY());
    }

    @Test
    public void intermediateSafeZoneUpdatesRespawnPoint() {
        LevelConfig c = minimalConfig();
        SafeZone mid = new SafeZone(10, 10, 60, 60, SafeZone.Type.INTERMEDIATE);
        c.entities.add(mid);
        Board board = boardSingle(c);
        Player player = board.getPlayer();
        player.setX(mid.getX() + 1);
        player.setY(mid.getY() + 1);
        board.update();
        player.hit();
        assertEquals("Tras zona intermedia el respawn debe actualizarse",
            mid.getX(), player.getX());
    }

    // ── Monedas ──

    @Test
    public void collectingCoinDeactivatesIt() {
        LevelConfig c = configWithCoin();
        Board board = boardSingle(c);
        Player player = board.getPlayer();
        Coin coin = (Coin) board.getCollectibles().get(0);
        player.setX(coin.getX());
        player.setY(coin.getY());
        board.update();
        assertFalse("La moneda debe desactivarse al recogerla", coin.isActive());
    }

    @Test
    public void collectingCoinIncrementsCount() {
        LevelConfig c = configWithCoin();
        Board board = boardSingle(c);
        Player player = board.getPlayer();
        Coin coin = (Coin) board.getCollectibles().get(0);
        player.setX(coin.getX());
        player.setY(coin.getY());
        board.update();
        assertEquals("Debe contabilizarse 1 moneda recogida", 1, board.getCoinsCollected());
    }

    @Test
    public void levelNotCompletedWithRemainingCoins() {
        LevelConfig c = configWithCoin();
        Board board = boardSingle(c);
        Player player = board.getPlayer();
        SafeZone finalZone = null;
        for (SafeZone z : board.getSafeZones()) {
            if (z.isFinal()) { finalZone = z; break; }
        }
        assertNotNull("Debe existir una zona final", finalZone);
        player.setX(finalZone.getX() + 1);
        player.setY(finalZone.getY() + 1);
        board.update();
        assertFalse("El nivel NO debe completarse si quedan monedas", board.isLevelCompleted());
    }

    @Test
    public void levelCompletesAfterAllCoinsAndFinalZone() {
        LevelConfig c = configWithCoin();
        Board board = boardSingle(c);
        Player player = board.getPlayer();
        Coin coin = (Coin) board.getCollectibles().get(0);
        player.setX(coin.getX());
        player.setY(coin.getY());
        board.update();
        SafeZone finalZone = null;
        for (SafeZone z : board.getSafeZones()) {
            if (z.isFinal()) { finalZone = z; break; }
        }
        player.setX(finalZone.getX() + 1);
        player.setY(finalZone.getY() + 1);
        board.update();
        assertTrue("El nivel debe completarse al recoger todas las monedas y llegar a zona final",
            board.isLevelCompleted());
    }

    // ── Timer ──  [BUG R7]

    @Test
    public void timeoutAddsDeathToPlayer() {
        Board board = boardSingle(minimalConfig());
        while (!board.tickTime()) { /* vacío */ }
        board.resetLevel();
        assertEquals("BUG R7: Al agotarse el tiempo debe sumarse 1 muerte",
            1, board.getTotalDeaths());
    }

    @Test
    public void timeoutResetsTimer() {
        LevelConfig c = minimalConfig();
        c.timeLimit = 10;
        Board board = boardSingle(c);
        while (!board.tickTime()) { /* vacío */ }
        board.resetLevel();
        assertEquals("Tras timeout el timer debe reiniciarse al límite original",
            10, board.getTimeRemaining());
    }

    // ═════════════════════════════════════════════
    //  MÓDULO 2 — MULTIJUGADOR
    // ═════════════════════════════════════════════

    @Test
    public void pvpCreatesTwoPlayers() {
        GameModeConfigurator cfg = new GameModeConfigurator(700, 500);
        GameState state = cfg.configurePvP(
            minimalConfig(),
            "Alice", "RED", Color.RED,
            "Bob",   "RED", Color.BLUE
        );
        assertEquals("PvP debe tener 2 jugadores", 2, state.getPlayers().size());
    }

    @Test
    public void pvpPlayersStartAtDifferentPositions() {
        GameModeConfigurator cfg = new GameModeConfigurator(700, 500);
        GameState state = cfg.configurePvP(
            minimalConfig(),
            "Alice", "RED", Color.RED,
            "Bob",   "RED", Color.BLUE
        );
        Player p1 = state.getPlayers().get(0);
        Player p2 = state.getPlayers().get(1);
        assertFalse("Los dos jugadores no deben empezar en la misma posición",
            p1.getX() == p2.getX() && p1.getY() == p2.getY());
    }

    // BUG R10: siempre gana jugador 1
    @Test
    public void pvpBobHasFinalZoneAssigned() {
        GameModeConfigurator cfg = new GameModeConfigurator(700, 500);
        GameState state = cfg.configurePvP(
            configWithCoin(),
            "Alice", "RED", Color.RED,
            "Bob",   "RED", Color.BLUE
        );
        SafeZone bobZone = null;
        for (SafeZone z : state.getSafeZones()) {
            if (z.isFinal() && "Bob".equals(z.getOwnerName())) {
                bobZone = z;
                break;
            }
        }
        assertNotNull("BUG R10: Debe existir una zona final asignada a Bob en PvP", bobZone);
    }

    @Test
    public void pvpWinnerIsFirstToRegister() {
        GameModeConfigurator cfg = new GameModeConfigurator(700, 500);
        GameState state = cfg.configurePvP(
            configWithCoin(),
            "Alice", "RED", Color.RED,
            "Bob",   "RED", Color.BLUE
        );
        state.playersReachedFinal.add("Bob");
        state.levelCompleted = true;
        assertEquals("BUG R10: Bob llegó primero, debe ser el ganador registrado",
            "Bob", state.playersReachedFinal.get(0));
    }

    // ── Colisión entre jugadores ──

    @Test
    public void playerCollisionAddsMuertesToBoth() {
        ScoreController score = new ScoreController();
        CollisionController cc = new CollisionController(List.of());
        Player p1 = PlayerFactory.createPlayer("RED", "P1", 100, 100, 20, 3);
        Player p2 = PlayerFactory.createPlayer("RED", "P2", 100, 100, 20, 3);
        cc.checkPlayerCollision(p1, p2, score);
        assertEquals("P1 debe registrar 1 muerte", 1, score.getPlayerDeaths("P1"));
        assertEquals("P2 debe registrar 1 muerte", 1, score.getPlayerDeaths("P2"));
    }

    @Test
    public void playersRespawnAtTheirOwnSpawnAfterCollision() {
        ScoreController score = new ScoreController();
        CollisionController cc = new CollisionController(List.of());
        Player p1 = PlayerFactory.createPlayer("RED", "P1", 50, 50, 20, 3);
        Player p2 = PlayerFactory.createPlayer("RED", "P2", 400, 400, 20, 3);
        p2.setSpawnX(400); p2.setSpawnY(400);
        p2.setX(50); p2.setY(50);
        cc.checkPlayerCollision(p1, p2, score);
        assertEquals("P1 debe reaparecer en spawnX=50",  50,  p1.getX());
        assertEquals("P1 debe reaparecer en spawnY=50",  50,  p1.getY());
        assertEquals("P2 debe reaparecer en spawnX=400", 400, p2.getX());
        assertEquals("P2 debe reaparecer en spawnY=400", 400, p2.getY());
    }

    // ── Puntaje ──

    @Test
    public void playerScoresAreIndependent() {
        ScoreController sc = new ScoreController();
        sc.addCoin("Alice"); sc.addCoin("Alice");
        sc.addCoin("Bob");   sc.addDeath("Bob");
        assertEquals("Alice debe tener 2 monedas",  2, sc.getPlayerCoins("Alice"));
        assertEquals("Bob debe tener 1 moneda",     1, sc.getPlayerCoins("Bob"));
        assertEquals("Alice no debe tener muertes", 0, sc.getPlayerDeaths("Alice"));
        assertEquals("Bob debe tener 1 muerte",     1, sc.getPlayerDeaths("Bob"));
    }

    @Test
    public void calculatePlayerScoreFormula() {
        ScoreController sc = new ScoreController();
        sc.addCoin("Alice"); sc.addCoin("Alice");
        sc.addDeath("Alice");
        assertEquals("Fórmula: monedas*10 - muertes*5 = 15",
            15, sc.calculatePlayerScore("Alice"));
    }

    // ═════════════════════════════════════════════
    //  MÓDULO 3 — PERSONAJES Y ENEMIGOS
    // ═════════════════════════════════════════════

    @Test
    public void bluePlayerHas1_5xSpeed() {
        int base = 4;
        Player blue = new BluePlayer("B", 0, 0, 20, base);
        assertEquals("BluePlayer debe tener speed = baseSpeed * 1.5",
            Math.round(base * 1.5f), blue.getSpeed());
    }

    @Test
    public void bluePlayerHas1_5xSize() {
        int size = 20;
        Player blue = new BluePlayer("B", 0, 0, size, 3);
        assertEquals("BluePlayer debe tener ancho = size * 1.5",
            Math.round(size * 1.5f), blue.getWidth());
    }

    // BUG R14 — Clyde

    @Test
    public void greenPlayerStartsWithShield() {
        GreenPlayer green = new GreenPlayer("Clyde", 100, 100, 20, 3);
        assertTrue("Clyde debe empezar con escudo activo", green.isShieldActive());
    }

    @Test
    public void greenPlayerFirstHitLosesShieldNoDeath() {
        GreenPlayer green = new GreenPlayer("Clyde", 100, 100, 20, 3);
        green.hit();
        assertFalse("BUG R14: Tras el primer golpe el escudo debe desactivarse",
            green.isShieldActive());
        assertEquals("BUG R14: Clyde NO debe reaparecer en el primer golpe",
            100, green.getX());
    }

    @Test
    public void greenPlayerFirstHitReducesSpeed() {
        int base = 4;
        GreenPlayer green = new GreenPlayer("Clyde", 100, 100, 20, base);
        green.hit();
        assertEquals("BUG R14: Tras primer golpe la velocidad debe bajar a 0.7×",
            Math.round(base * 0.7f), green.getSpeed());
    }

    @Test
    public void greenPlayerSecondHitCausesRespawn() {
        GreenPlayer green = new GreenPlayer("Clyde", 100, 100, 20, 3);
        green.hit();
        green.setX(300); green.setY(300);
        green.hit();
        assertEquals("En el segundo golpe Clyde debe reaparecer en spawnX", 100, green.getX());
        assertEquals("En el segundo golpe Clyde debe reaparecer en spawnY", 100, green.getY());
    }

    @Test
    public void greenPlayerRestoresShieldAndSpeedOnRespawn() {
        int base = 4;
        GreenPlayer green = new GreenPlayer("Clyde", 100, 100, 20, base);
        green.hit();
        green.hit();
        assertTrue("Clyde debe recuperar el escudo al reaparecer", green.isShieldActive());
        assertEquals("Clyde debe recuperar su velocidad base al reaparecer",
            base, green.getSpeed());
    }

    @Test
    public void enemyCollisionNoDeathWhenShieldAbsorbs() {
        ScoreController score = new ScoreController();
        Enemy enemy = new Enemy(0, 0, 20, 1);
        GreenPlayer green = new GreenPlayer("Clyde", 0, 0, 20, 3);
        enemy.onPlayerCollision(green, score);
        assertEquals("BUG R14: No debe contarse muerte cuando el escudo absorbe",
            0, score.getPlayerDeaths("Clyde"));
    }

    @Test
    public void enemyCollisionCountsDeathOnSecondHit() {
        ScoreController score = new ScoreController();
        Enemy enemy = new Enemy(0, 0, 20, 1);
        GreenPlayer green = new GreenPlayer("Clyde", 0, 0, 20, 3);
        enemy.onPlayerCollision(green, score); // consume escudo
        enemy.onPlayerCollision(green, score); // golpe real
        assertEquals("El segundo golpe de enemigo debe registrar 1 muerte",
            1, score.getPlayerDeaths("Clyde"));
    }

    // ── Enemigos ──

    @Test
    public void horizontalEnemyBounces() {
        Enemy e = new Enemy(100, 100, 15, 3);
        e.setMovement(new HorizontalMovement(e, 80, 200, 3));
        boolean reversed = false;
        for (int tick = 0; tick < 200 && !reversed; tick++) {
            int before = e.getX();
            e.update();
            if (e.getX() < before) reversed = true;
            assertTrue("Tick " + tick + ": enemigo fuera de límites",
                e.getX() >= 80 && e.getX() + 15 <= 200);
        }
        assertTrue("El enemigo horizontal debe haber rebotado al menos una vez", reversed);
    }

    @Test
    public void verticalEnemyBounces() {
        Enemy e = new Enemy(100, 100, 15, 3);
        e.setMovement(new VerticalMovement(e, 80, 200, 3));
        boolean reversed = false;
        for (int tick = 0; tick < 200 && !reversed; tick++) {
            int before = e.getY();
            e.update();
            if (e.getY() < before) reversed = true;
            assertTrue("Tick " + tick + ": enemigo fuera de límites verticales",
                e.getY() >= 80 && e.getY() + 15 <= 200);
        }
        assertTrue("El enemigo vertical debe haber rebotado al menos una vez", reversed);
    }

    @Test
    public void acceleratedEnemyMovesFaster() {
        Enemy normal = new Enemy(0, 100, 15, 2);
        normal.setMovement(new HorizontalMovement(normal, 0, 500, 2));
        Enemy accel = new Enemy(0, 200, 15, 4);
        accel.setMovement(new HorizontalMovement(accel, 0, 500, 4));
        for (int i = 0; i < 5; i++) { normal.update(); accel.update(); }
        assertTrue("El enemigo acelerado debe recorrer más distancia",
            accel.getX() > normal.getX());
    }

    // ═════════════════════════════════════════════
    //  MÓDULO 4 — ELEMENTOS ESPECIALES
    // ═════════════════════════════════════════════

    @Test
    public void lifeSourceDeactivatesAfterCollection() {
        ScoreController sc = new ScoreController();
        LifeSource life = new LifeSource(50, 50, 20, 20);
        Player player = new RedPlayer("P", 50, 50, 20, 3);
        life.onPlayerCollision(player, sc);
        assertFalse("LifeSource debe desactivarse tras ser recogida", life.isActive());
    }

    @Test
    public void lifeSourceAddsOneLife() {
        ScoreController sc = new ScoreController();
        LifeSource life = new LifeSource(50, 50, 20, 20);
        Player player = new RedPlayer("P", 50, 50, 20, 3);
        life.onPlayerCollision(player, sc);
        assertEquals("LifeSource debe agregar 1 vida", 1, sc.getPlayerLives("P"));
    }

    @Test
    public void bombKillsPlayerAndDeactivates() {
        ScoreController sc = new ScoreController();
        Bomb bomb = new Bomb(50, 50, 20, 20);
        Player player = new RedPlayer("P", 50, 50, 20, 3);
        bomb.onPlayerCollision(player, sc);
        assertFalse("La bomba debe desactivarse tras matar al jugador", bomb.isActive());
        assertEquals("La bomba debe registrar 1 muerte", 1, sc.getPlayerDeaths("P"));
    }

    @Test
    public void bombStoresEnemyReferenceOnEnemyCollision() {
        Bomb bomb = new Bomb(50, 50, 20, 20);
        Enemy enemy = new Enemy(50, 50, 15, 2);
        bomb.onEnemyCollision(enemy);
        assertSame("La bomba debe guardar referencia al enemigo", enemy, bomb.getTriggeringEnemy());
        assertFalse("La bomba debe desactivarse al ser tocada por enemigo", bomb.isActive());
    }

    @Test
    public void bombNotRequiredForCompletion() {
        Bomb bomb = new Bomb(50, 50, 20, 20);
        assertFalse("Una bomba NO debe ser requerida para completar el nivel",
            bomb.isRequiredForCompletion());
    }

    @Test
    public void coinIsRequiredForCompletion() {
        Coin coin = new Coin(100, 100, 15);
        assertTrue("Una moneda amarilla SÍ debe ser requerida para completar el nivel",
            coin.isRequiredForCompletion());
    }

    // ═════════════════════════════════════════════
    //  EXTENSIBILIDAD (CA-3)
    // ═════════════════════════════════════════════

    @Test
    public void playerFactoryAcceptsNewPlayerType() {
        PlayerFactory.register("PURPLE", (name, x, y, size, speed) ->
            new RedPlayer(name, x, y, size, speed) {
                @Override public Color getPlayerColor() { return new Color(128, 0, 128); }
            }
        );
        Player purple = PlayerFactory.createPlayer("PURPLE", "Test", 0, 0, 20, 3);
        assertNotNull("PlayerFactory debe crear jugadores de tipo personalizado", purple);
        assertEquals(new Color(128, 0, 128), purple.getPlayerColor());
    }

    @Test
    public void customCollectibleHandlesCollision() {
        final boolean[] touched = {false};
        Collectible custom = new Collectible(50, 50, 20, 20, false) {
            @Override
            public void onPlayerCollision(Player player, ScoreController score) {
                touched[0] = true;
                consume();
            }
            @Override public Color getColor() { return Color.MAGENTA; }
        };
        Player p = new RedPlayer("P", 50, 50, 20, 3);
        ScoreController sc = new ScoreController();
        custom.onPlayerCollision(p, sc);
        assertTrue("El collectible personalizado debe ejecutar su lógica", touched[0]);
        assertFalse("El collectible personalizado debe desactivarse", custom.isActive());
    }
}