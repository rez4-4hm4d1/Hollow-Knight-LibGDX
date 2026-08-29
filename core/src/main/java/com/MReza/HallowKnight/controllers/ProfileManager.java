package com.MReza.HallowKnight.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;

public class ProfileManager {
    private static final String PROFILE_FILE = "profile.json";
    private static Json json = new Json();
    private static ProfileData data;

    public static class ProfileData {
        public ArrayList<String> unlockedAchievements = new ArrayList<>();
        public ArrayList<String> equippedCharms = new ArrayList<>();
        public ArrayList<String> killedEnemyTypes = new ArrayList<>();
    }

    public static void loadProfile() {
        FileHandle file = Gdx.files.local(PROFILE_FILE);
        if (file.exists()) {
            data = json.fromJson(ProfileData.class, file.readString());
        } else {
            data = new ProfileData();
            saveProfile();
        }
    }

    public static void saveProfile() {
        FileHandle file = Gdx.files.local(PROFILE_FILE);
        file.writeString(json.prettyPrint(data), false);
    }

    public static ProfileData getData() {
        if (data == null) loadProfile();
        return data;
    }

    public static boolean isAchievementUnlocked(String id) {
        return getData().unlockedAchievements.contains(id);
    }

    public static boolean unlockAchievement(String id) {
        if (!isAchievementUnlocked(id)) {
            getData().unlockedAchievements.add(id);
            saveProfile();
            return true; // it is just_open
        }
        return false; // it was open before
    }

    public static boolean toggleCharm(String charmName) {
        ProfileData data = getData();

        if (data.equippedCharms.contains(charmName)) {
            data.equippedCharms.remove(charmName);
            saveProfile();
            return true;
        }
        else if (data.equippedCharms.size() < 3) {
            data.equippedCharms.add(charmName);
            saveProfile();
            return true;
        }
        return false;
    }
    public static boolean isCharmEquipped(String charmName) {
        return getData().equippedCharms.contains(charmName);
    }
}
