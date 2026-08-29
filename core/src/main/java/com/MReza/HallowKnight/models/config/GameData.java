package com.MReza.HallowKnight.models.config;


import com.badlogic.gdx.utils.Array;

public class GameData {
    private float playerX;
    private float playerY;
    private String mapName;
    private int health;
    private int soul;

    private int deaths;
    private int enemiesKilled;
    private float playTime;

    private Array<Boolean> enemyDeadStates = new Array<>();

    // for JSON
    public GameData() {
    }

    public float getPlayerX() {
        return playerX;
    }

    public void setPlayerX(float playerX) {
        this.playerX = playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public void setPlayerY(float playerY) {
        this.playerY = playerY;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getSoul() {
        return soul;
    }

    public void setSoul(int soul) {
        this.soul = soul;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void setEnemiesKilled(int enemiesKilled) {
        this.enemiesKilled = enemiesKilled;
    }

    public float getPlayTime() {
        return playTime;
    }

    public void setPlayTime(float playTime) {
        this.playTime = playTime;
    }

    public Array<Boolean> getEnemyDeadStates() {
        return enemyDeadStates;
    }

    public void setEnemyDeadStates(Array<Boolean> enemyDeadStates) {
        this.enemyDeadStates = enemyDeadStates;
    }
}
