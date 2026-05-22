package domain;

public interface Collidable {
    void onPlayerCollision(Player player, ScoreController score);
}