package com.MReza.HallowKnight.models.config;

import com.MReza.HallowKnight.models.enemies.Enemy;
import com.MReza.HallowKnight.models.environment.MapType;
import com.MReza.HallowKnight.models.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class GameConfig {

    private static final Json json = new Json();

    private static FileHandle getFile(int slotNumber) {
        return Gdx.files.local("HallowKnight_SaveSlot_" + slotNumber + ".json");
    }

    public static boolean hasSave(int slotNumber) {
        return getFile(slotNumber).exists();
    }

    public static void saveGame(int slotNumber, MapType currentMap, Player player, Array<Enemy> enemies) {
        GameData data = new GameData();
        data.setMapName(currentMap.name()); ;
        data.setPlayerX(player.getX());
        data.setPlayerY(player.getY());
        data.setHealth(player.getCurrentHealth());
        data.setSoul(player.getCurrentSoul());

        data.setDeaths(player.getDeaths());
        data.setEnemiesKilled(player.getEnemiesKilled());
        data.setPlayTime(player.getPlayTime());

        for (Enemy enemy : enemies) {
            data.getEnemyDeadStates().add(enemy.isDead());
        }

        String jsonText = json.toJson(data);
        getFile(slotNumber).writeString(jsonText, false);
    }

    public static void loadEnemyStates(int slotNumber, Array<Enemy> enemies) {
        FileHandle file = getFile(slotNumber);
        if (file.exists()) {
            GameData data = json.fromJson(GameData.class, file.readString());
            if (data.getEnemyDeadStates() != null) {
                for (int i = 0; i < enemies.size && i < data.getEnemyDeadStates().size; i++) {
                    if (data.getEnemyDeadStates().get(i)) {
                        enemies.get(i).setDead(true);
                    }
                }
            }
        }
    }

    public static MapType loadMap(int slotNumber) {
        FileHandle file = getFile(slotNumber);
        if (file.exists()) {
            GameData data = json.fromJson(GameData.class, file.readString());
            return MapType.valueOf(data.getMapName());
        }
        if (slotNumber == 1 || slotNumber == 2) return MapType.FORGOTTEN_CROSSROADS;
        return MapType.GREEN_PATH;
    }

    public static void loadPlayerState(int slotNumber, Player player) {
        FileHandle file = getFile(slotNumber);
        if (file.exists()) {
            GameData data = json.fromJson(GameData.class, file.readString());
            player.setX(data.getPlayerX());
            player.setY(data.getPlayerY());
            int health = (data.getHealth() > 0) ? data.getHealth() : player.getMaxHealth();
            player.setCurrentHealth(health);
            player.setCurrentSoul(data.getSoul());

            player.setDeaths(data.getDeaths());
            player.setEnemiesKilled(data.getEnemiesKilled());
            player.setPlayTime(data.getPlayTime());

        } else {
            player.setX(100f);
            player.setY(200f);
            player.setCurrentHealth(player.getMaxHealth());
            player.setCurrentSoul(0);
        }
    }
}
