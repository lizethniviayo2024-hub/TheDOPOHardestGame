package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

import domain.PlayerType;

public class HardestGameGUI extends JFrame {

	private JPanel container;
	private CardLayout cardLayout;

	// MENU
	private JButton btnPlayGame;
	private JButton btnLeaderBoard;
	private JButton btnExitGame;

	// GAME
	private GamePanel gamePanel;

	private JLabel lblDeaths;
	private JLabel lblCoins;
	private JLabel lblTime;
	private JLabel lblLevel;

	private JLabel lblPlayer1Info;
	private JLabel lblPlayer2Info;

	private int currentLevel = 1;
	private String player1Name = "Player 1";
	private String player2Name = "Player 2";

	private String player1SkinType = "RED";
	private Color player1Border = Color.BLACK;
	private int totalDeathsAccum = 0;
	private int totalCoinsAccum = 0;
	private int totalP1DeathsAccum = 0;
	private int totalP1CoinsAccum = 0;
	private int totalP2DeathsAccum = 0;
	private int totalP2CoinsAccum = 0;
	// MODO ACTUAL
	private GameModeData currentGameMode;

	// CONSTRUCTOR

	public HardestGameGUI() {

		setTitle("The DOPO Hardest Game");

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		prepareElements();

		prepareActions();
	}

	// PREPARE

	private void prepareElements() {

		setSize(800, 550);

		setLocationRelativeTo(null);

		JPanel outerPanel = new JPanel(new BorderLayout());

		outerPanel.setBackground(Color.WHITE);

		outerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

		cardLayout = new CardLayout();

		container = new JPanel(cardLayout);

		container.add(prepareElementsMenu(), "MENU");
		container.add(prepareElementsMode(), "MODE");
		container.add(prepareElementsLEADERBOARD(), "LEADERBOARD");
		container.add(prepareElementsONEPLAY(), "ONEPLAY");
		container.add(prepareElementsLEVELS(), "LEVELS");

		outerPanel.add(container, BorderLayout.CENTER);

		add(outerPanel);

		cardLayout.show(container, "MENU");
	}

	// MENU

	private JPanel prepareElementsMenu() {

		JPanel menuPanel = new JPanel(new BorderLayout());

		menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

		// TITLE
		JPanel titlePanel = new JPanel(new BorderLayout());

		titlePanel.setBackground(new Color(180, 190, 230));

		titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 0, 30));

		JLabel lblSubtitulo = new JLabel("The DOPO...");

		lblSubtitulo.setFont(new Font("Arial Black", Font.BOLD, 18));

		lblSubtitulo.setForeground(new Color(50, 50, 100));

		JLabel lblTitulo = new JLabel("HARDEST GAME");

		lblTitulo.setFont(new Font("Arial Black", Font.BOLD, 72));

		lblTitulo.setForeground(new Color(70, 120, 200));

		titlePanel.add(lblSubtitulo, BorderLayout.NORTH);
		titlePanel.add(lblTitulo, BorderLayout.CENTER);

		menuPanel.add(titlePanel, BorderLayout.NORTH);

		// BUTTONS
		JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 10));

		btnPanel.setBackground(new Color(180, 190, 230));

		btnPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 60, 80));

		btnPlayGame = createMenuButton("PLAY GAME", Color.RED);

		btnLeaderBoard = createMenuButton("LEADER BOARD", new Color(70, 120, 200));

		btnExitGame = createMenuButton("EXIT GAME", new Color(34, 139, 34));

		btnPanel.add(btnPlayGame);
		btnPanel.add(btnLeaderBoard);
		btnPanel.add(btnExitGame);

		menuPanel.add(btnPanel, BorderLayout.CENTER);

		return menuPanel;
	}

	// MODES

	private JPanel prepareElementsMode() {

		JPanel modePanel = new JPanel(new BorderLayout());

		modePanel.setBackground(new Color(100, 100, 230));

		modePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

		JLabel lblTitle = new JLabel("SELECT MODE", JLabel.CENTER);

		lblTitle.setFont(new Font("Arial Black", Font.BOLD, 20));

		lblTitle.setForeground(Color.WHITE);

		lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

		modePanel.add(lblTitle, BorderLayout.NORTH);

		JPanel btnPanel = new JPanel(new GridLayout(4, 1, 10, 10));

		btnPanel.setBackground(new Color(180, 190, 230));

		btnPanel.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

		JButton btnPlayer = createMenuButton("PLAYER", Color.RED);

		JButton btnPVP = createMenuButton("PLAYER VS PLAYER", Color.BLUE);

		JButton btnPVM = createMenuButton("PLAYER VS MACHINE", Color.GREEN);

		JButton btnBack = createMenuButton("BACK", Color.BLACK);

		btnPanel.add(btnPlayer);
		btnPanel.add(btnPVP);
		btnPanel.add(btnPVM);
		btnPanel.add(btnBack);

		modePanel.add(btnPanel, BorderLayout.CENTER);

		// ACTIONS

		btnBack.addActionListener(e -> cardLayout.show(container, "MENU"));

		btnPlayer.addActionListener(e -> {

			currentGameMode = new GameModeData(GameModeData.Mode.SINGLE_PLAYER, 1);

			cardLayout.show(container, "LEVELS");
		});

		btnPVP.addActionListener(e -> {

			currentGameMode = new GameModeData(GameModeData.Mode.PLAYER_VS_PLAYER, 1);

			cardLayout.show(container, "LEVELS");
		});

		btnPVM.addActionListener(e -> {

			String[] options = { "IA Aleatoria", "IA Experta" };

			int choice = JOptionPane.showOptionDialog(this, "Selecciona el tipo de IA:", "Modo vs Máquina",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if (choice == 0) {

				currentGameMode = new GameModeData(GameModeData.Mode.PLAYER_VS_AI_RANDOM, 1);

			} else if (choice == 1) {

				currentGameMode = new GameModeData(GameModeData.Mode.PLAYER_VS_AI_EXPERT, 1);

			} else {
				return;
			}

			cardLayout.show(container, "LEVELS");
		});

		return modePanel;
	}

	// LEADERBOARD

	private JPanel prepareElementsLEADERBOARD() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBackground(new Color(100, 100, 230));

		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

		JLabel title = new JLabel("LEADERBOARD", JLabel.CENTER);

		title.setFont(new Font("Arial Black", Font.BOLD, 30));

		title.setForeground(Color.WHITE);

		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

		panel.add(title, BorderLayout.NORTH);

		String[] columns = { "Player", "Deaths" };

		DefaultTableModel model = new DefaultTableModel(columns, 0);

		JTable table = new JTable(model);

		JScrollPane scrollPane = new JScrollPane(table);

		panel.add(scrollPane, BorderLayout.CENTER);

		JButton btnBack = createMenuButton("BACK", Color.BLACK);

		btnBack.addActionListener(e -> cardLayout.show(container, "MENU"));

		panel.add(btnBack, BorderLayout.SOUTH);

		return panel;
	}

	// LEVELS

	private JPanel prepareElementsLEVELS() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBackground(new Color(100, 100, 230));

		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

		JLabel title = new JLabel("SELECT LEVEL", JLabel.CENTER);

		title.setFont(new Font("Arial Black", Font.BOLD, 30));

		title.setForeground(Color.WHITE);

		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

		panel.add(title, BorderLayout.NORTH);

		JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));

		grid.setBackground(new Color(180, 190, 230));

		grid.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

		JButton lvl1 = createMenuButton("LEVEL 1", Color.RED);
		JButton lvl2 = createMenuButton("LEVEL 2", Color.BLUE);
		JButton lvl3 = createMenuButton("LEVEL 3", Color.GREEN);
		JButton lvl4 = createMenuButton("LEVEL 4", new Color(150, 0, 150));
		JButton lvl5 = createMenuButton("LEVEL 5", new Color(200, 100, 0));

		grid.add(lvl1);
		grid.add(lvl2);
		grid.add(lvl3);
		grid.add(lvl4);
		grid.add(lvl5);

		panel.add(grid, BorderLayout.CENTER);

		JButton btnBack = createMenuButton("BACK", Color.BLACK);

		panel.add(btnBack, BorderLayout.SOUTH);

		btnBack.addActionListener(e -> cardLayout.show(container, "MODE"));

		lvl1.addActionListener(e -> startSelectedLevel(1));
		lvl2.addActionListener(e -> startSelectedLevel(2));
		lvl3.addActionListener(e -> startSelectedLevel(3));
		lvl4.addActionListener(e -> startSelectedLevel(4));
		lvl5.addActionListener(e -> startSelectedLevel(5));

		return panel;
	}

	// GAME SCREEN

	private JPanel prepareElementsONEPLAY() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBackground(new Color(100, 100, 230));

		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

		// NORTH
		JPanel northPanel = new JPanel(new BorderLayout());

		northPanel.setBackground(new Color(180, 190, 230));

		lblLevel = new JLabel("LEVEL 1", JLabel.CENTER);

		lblLevel.setFont(new Font("Arial Black", Font.BOLD, 24));

		lblLevel.setForeground(Color.WHITE);

		lblLevel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

		JPanel hudPanel = new JPanel(new GridLayout(1, 5));

		hudPanel.setBackground(new Color(230, 190, 190));

		lblDeaths = new JLabel("Deaths: 0");
		lblCoins = new JLabel("Coins: 0/0");
		lblTime = new JLabel("Time: 0", JLabel.CENTER);
		lblPlayer1Info = new JLabel("", JLabel.CENTER);
		lblPlayer2Info = new JLabel("", JLabel.CENTER);

		hudPanel.add(lblPlayer1Info);
		hudPanel.add(lblPlayer2Info);
		hudPanel.add(lblDeaths);
		hudPanel.add(lblCoins);
		hudPanel.add(lblTime);

		northPanel.add(lblLevel, BorderLayout.NORTH);
		northPanel.add(hudPanel, BorderLayout.CENTER);

		panel.add(northPanel, BorderLayout.NORTH);

		// GAME PANEL

		gamePanel = new GamePanel();

		gamePanel.setPreferredSize(new Dimension(500, 300));

		gamePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

		gamePanel.setOnHudUpdate(() -> {

			SwingUtilities.invokeLater(() -> {

				if (currentGameMode.getMode() == GameModeData.Mode.PLAYER_VS_PLAYER) {

					// HUD PvP

					lblDeaths.setText("");
					lblCoins.setText("");

					lblPlayer1Info.setText(

							player1Name + " | Deaths: " + gamePanel.getPlayerDeaths(player1Name) + " | Coins: "
									+ gamePanel.getPlayerCoins(player1Name)

					);

					lblPlayer2Info.setText(

							player2Name + " | Deaths: " + gamePanel.getPlayerDeaths(player2Name) + " | Coins: "
									+ gamePanel.getPlayerCoins(player2Name)

					);

				} else {

					// HUD SINGLE PLAYER

					lblDeaths.setText(player1Name + " | Deaths: " + gamePanel.getDeaths());

					lblCoins.setText("Coins: " + gamePanel.getCoinsCollected() + "/" + gamePanel.getTotalCoins());

					lblPlayer1Info.setText("");
					lblPlayer2Info.setText("");
				}
				lblTime.setText("Time: " + gamePanel.getTimeRemaining());
			});
		});

		gamePanel.setOnLevelComplete(() -> {

			SwingUtilities.invokeLater(() -> {

				showLevelCompleteDialog();
			});
		});

		gamePanel.setOnTimeOut(() -> {
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(HardestGameGUI.this, "¡Tiempo agotado!", "Game Over",
						JOptionPane.WARNING_MESSAGE);
				gamePanel.timeoutReset();
				gamePanel.resumeTimers();
			});
		});

		gamePanel.setOnPause(() -> {

			SwingUtilities.invokeLater(() -> {

				showPauseDialog();
			});
		});

		// Manejar salida al menú por tecla ESC o acción externa
		gamePanel.setOnExitToMenu(() -> {
			SwingUtilities.invokeLater(() -> {
				gamePanel.stopGame();
				cardLayout.show(container, "LEVELS");
			});
		});

		JPanel centerWrapper = new JPanel(new GridBagLayout());

		centerWrapper.setBackground(new Color(100, 100, 230));

		centerWrapper.add(gamePanel);

		panel.add(centerWrapper, BorderLayout.CENTER);

		// SOUTH

		JPanel southPanel = new JPanel(new BorderLayout());

		southPanel.setBackground(new Color(180, 190, 230));

		// Botones de control: Pause y Volver al menú
		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		controls.setOpaque(false);

		JButton btnPause = createMenuButton("PAUSE", new Color(70, 120, 200));
		JButton btnBack = createMenuButton("BACK", Color.BLACK);

		btnPause.addActionListener(e -> {
			gamePanel.togglePauseExtern();
		});

		btnBack.addActionListener(e -> {
			gamePanel.stopGame();
			cardLayout.show(container, "LEVELS");
		});

		controls.add(btnPause);
		controls.add(btnBack);

		southPanel.add(controls, BorderLayout.EAST);

		panel.add(southPanel, BorderLayout.SOUTH);

		return panel;
	}

	// START LEVEL

	private void startSelectedLevel(int level) {

		currentLevel = level;
		totalDeathsAccum = 0;
		totalCoinsAccum = 0;
		totalP1DeathsAccum = 0;
		totalP1CoinsAccum = 0;
		totalP2DeathsAccum = 0;
		totalP2CoinsAccum = 0;

		currentGameMode.setLevel(level);

		startGameWithMode();

		cardLayout.show(container, "ONEPLAY");
	}

	// START GAME

	private void startGameWithMode() {

		if (currentGameMode == null) {
			return;
		}

		try {

			GameModeData.Mode mode = currentGameMode.getMode();

			int level = currentGameMode.getLevel();

			switch (mode) {

			case SINGLE_PLAYER: {
				String nombre = JOptionPane.showInputDialog(this, "Enter your name:");
				if (nombre == null || nombre.isBlank())
					nombre = "Player 1";
				player1Name = nombre; // ← guardar
				PlayerType p1Type = chooseSkin("Player 1 - Choose Skin");
				if (p1Type == null)
					return;
				Color border = chooseBorder("Choose Border", p1Type.getColor());
				player1SkinType = p1Type.name();
				player1Border = border;
				gamePanel.startLevel(level, player1Name, player1SkinType, player1Border);
				break;
			}

			case PLAYER_VS_PLAYER: {

				// NOMBRES DE LOS JUGADORES

				player1Name = JOptionPane.showInputDialog(null, "Enter Player 1 Name");

				player2Name = JOptionPane.showInputDialog(null, "Enter Player 2 Name");
				if (player1Name == null || player1Name.isBlank()) {
					player1Name = "Player 1";
				}

				if (player2Name == null || player2Name.isBlank()) {
					player2Name = "Player 2";
				}

				// SKINS

				PlayerType p1Type = chooseSkin("Player 1");

				if (p1Type == null) {
					return;
				}

				PlayerType p2Type = chooseSkin("Player 2");

				if (p2Type == null) {
					return;
				}

				Color p1Border = chooseBorder("P1 Border", p1Type.getColor());

				Color p2Border = chooseBorder("P2 Border", p2Type.getColor());

				// INICIAR JUEGO

				gamePanel.startLevelPvP(level, player1Name, p1Type.name(), p1Border,

						player2Name, p2Type.name(), p2Border);

				break;
			}

			case PLAYER_VS_AI_RANDOM: {

				PlayerType p1Type = chooseSkin("Choose Skin");

				if (p1Type == null) {
					return;
				}

				Color border = chooseBorder("Choose Border", p1Type.getColor());

				gamePanel.startLevelPvMAIRandom(level, p1Type.name(), border);

				break;
			}

			case PLAYER_VS_AI_EXPERT: {

				PlayerType p1Type = chooseSkin("Choose Skin");

				if (p1Type == null) {
					return;
				}

				Color border = chooseBorder("Choose Border", p1Type.getColor());

				gamePanel.startLevelPvMAIExpert(level, p1Type.name(), border);

				break;
			}
			}

		} catch (Exception e) {

			JOptionPane.showMessageDialog(this, "Error iniciando juego:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// START GAME WITH SAME PLAYERS (PvP)

	private void startGameWithModeSamePlayers() {

		if (currentGameMode == null || currentGameMode.getMode() != GameModeData.Mode.PLAYER_VS_PLAYER) {
			startGameWithMode();
			return;
		}

		try {

			int level = currentGameMode.getLevel();

			// SKINS (sin pedir nombres nuevamente)

			PlayerType p1Type = chooseSkin("Player 1");

			if (p1Type == null) {
				return;
			}

			PlayerType p2Type = chooseSkin("Player 2");

			if (p2Type == null) {
				return;
			}

			Color p1Border = chooseBorder("P1 Border", p1Type.getColor());

			Color p2Border = chooseBorder("P2 Border", p2Type.getColor());

			// INICIAR JUEGO CON MISMOS NOMBRES

			gamePanel.startLevelPvP(level, player1Name, p1Type.name(), p1Border, player2Name, p2Type.name(), p2Border);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(this, "Error iniciando juego:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// PAUSE

	private void showPauseDialog() {

		Object[] options = { "Reanudar", "Ir al menú" };

		int option = JOptionPane.showOptionDialog(this, "Juego pausado", "Pausa", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (option == 0) {
			// Reanudar
			gamePanel.setPaused(false);
			gamePanel.requestFocusInWindow();
		} else {
			// Volver al menú
			gamePanel.stopGame();

			cardLayout.show(container, "LEVELS");
		}
	}

	// LEVEL COMPLETE

	private void showLevelCompleteDialog() {

		String message;

		if (currentGameMode.getMode() == GameModeData.Mode.PLAYER_VS_PLAYER) {
			String winner = gamePanel.getPvPWinner();
			int winnerCoins = gamePanel.getPlayerCoins(winner);
			int winnerDeaths = gamePanel.getPlayerDeaths(winner);
			String otherPlayer;
			int otherPlayerCoins;
			int otherPlayerDeaths;

			// Acumular stats PvP por jugador
			totalP1CoinsAccum += gamePanel.getPlayerCoins(player1Name);
			totalP1DeathsAccum += gamePanel.getPlayerDeaths(player1Name);
			totalP2CoinsAccum += gamePanel.getPlayerCoins(player2Name);
			totalP2DeathsAccum += gamePanel.getPlayerDeaths(player2Name);

			if (player1Name.equals(winner)) {
				otherPlayer = player2Name;
				otherPlayerCoins = gamePanel.getPlayerCoins(player2Name);
				otherPlayerDeaths = gamePanel.getPlayerDeaths(player2Name);
			} else {
				otherPlayer = player1Name;
				otherPlayerCoins = gamePanel.getPlayerCoins(player1Name);
				otherPlayerDeaths = gamePanel.getPlayerDeaths(player1Name);
			}

			message = "¡" + winner + " ha ganado!\n\n" + "Ganador: " + winner + "\n" + "  Monedas: " + winnerCoins
					+ "\n" + "  Muertes: " + winnerDeaths + "\n\n" + "Perdedor: " + otherPlayer + "\n" + "  Monedas: "
					+ otherPlayerCoins + "\n" + "  Muertes: " + otherPlayerDeaths + "\n\n" + "¿Siguiente nivel?";
		} else {
			totalDeathsAccum += gamePanel.getDeaths();
			totalCoinsAccum += gamePanel.getPlayerCoins(player1Name);
			message = "¡Nivel completado!\n¿Siguiente nivel?";
		}

		int option = JOptionPane.showConfirmDialog(this, message, "Victory", JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {

			currentLevel++;

			if (currentLevel > 5) {
				if (currentGameMode.getMode() == GameModeData.Mode.PLAYER_VS_PLAYER) {
					JOptionPane.showMessageDialog(this,
							"¡Completaste todos los niveles!\n\n" + player1Name + " → Monedas: " + totalP1CoinsAccum
									+ " | Muertes: " + totalP1DeathsAccum + "\n" + player2Name + " → Monedas: "
									+ totalP2CoinsAccum + " | Muertes: " + totalP2DeathsAccum);
				} else {
					JOptionPane.showMessageDialog(this, "¡Completaste todos los niveles!\n\n" + player1Name
							+ " → Monedas: " + totalCoinsAccum + " | Muertes: " + totalDeathsAccum);
				}
				cardLayout.show(container, "MENU");
				return;
			}

			currentGameMode.setLevel(currentLevel);

			if (currentGameMode.getMode() == GameModeData.Mode.PLAYER_VS_PLAYER) {
				startGameWithModeSamePlayers();
			} else {
				gamePanel.startLevel(currentGameMode.getLevel(), player1Name, player1SkinType, player1Border);
			}

		} else {
			cardLayout.show(container, "LEVELS");
		}
	}

	// HELPERS

	private JButton createMenuButton(String text, Color color) {

		JButton btn = new OutlineButton(text, color, Color.BLACK, 2);

		btn.setForeground(Color.WHITE);

		btn.setFocusPainted(false);

		btn.setFont(new Font("Arial Black", Font.BOLD, 16));

		return btn;
	}

	private PlayerType chooseSkin(String title) {

		PlayerTypeSelectorDialog dlg = new PlayerTypeSelectorDialog(this, title);

		dlg.setVisible(true);

		return dlg.isConfirmed() ? dlg.getSelectedType() : null;
	}

	private Color chooseBorder(String title, Color initial) {

		ColorSelectorDialog dlg = new ColorSelectorDialog(this, title, initial);

		dlg.setVisible(true);

		return dlg.isConfirmed() ? dlg.getSelectedColor() : initial;
	}

	// ACTIONS

	private void prepareActions() {

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		btnExitGame.addActionListener(e -> exit());

		btnPlayGame.addActionListener(e -> cardLayout.show(container, "MODE"));

		btnLeaderBoard.addActionListener(e -> cardLayout.show(container, "LEADERBOARD"));
	}

	// EXIT

	private void exit() {

		int option = JOptionPane.showConfirmDialog(this, "¿Salir del juego?", "Confirmar", JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {

			System.exit(0);
		}
	}

	// MAIN

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {

			HardestGameGUI gui = new HardestGameGUI();

			gui.setVisible(true);
		});
	}
}