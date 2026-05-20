package domain;

public class PlayerProfile {

    private String name;
    private int deaths;
    private int coins;

    public PlayerProfile(String name) {
        this.name = name;
        this.deaths = 0;
        this.coins = 0;
    }

    public String getName() {
        return name;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        deaths++;
    }

    public int getCoins() {
        return coins;
    }

    public void addCoin() {
        coins++;
    }
}