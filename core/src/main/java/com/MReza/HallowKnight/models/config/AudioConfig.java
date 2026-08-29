package com.MReza.HallowKnight.models.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class AudioConfig {
    private static float musicVolume;
    private static boolean isMusicOn;
    private static boolean isSfxOn;

    private static Preferences prefs;

    public static void load() {
        prefs = Gdx.app.getPreferences("HollowKnight_Audio");

        musicVolume = prefs.getFloat("musicVolume", 0.5f);
        isMusicOn = prefs.getBoolean("isMusicOn", true);
        isSfxOn = prefs.getBoolean("isSfxOn", true);
    }

    public static void save() {
        prefs.putFloat("musicVolume", musicVolume);
        prefs.putBoolean("isMusicOn", isMusicOn);
        prefs.putBoolean("isSfxOn", isSfxOn);
        prefs.flush();
    }

    public static boolean isMusicOn() {
        return isMusicOn;
    }

    public static boolean isSfxOn() {
        return isSfxOn;
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static void setIsSfxOn(boolean isSfxOn) {
        AudioConfig.isSfxOn = isSfxOn;
    }

    public static void setMusicVolume(float musicVolume) {
        AudioConfig.musicVolume = musicVolume;
    }

    public static void setIsMusicOn(boolean isMusicOn) {
        AudioConfig.isMusicOn = isMusicOn;
    }
}
