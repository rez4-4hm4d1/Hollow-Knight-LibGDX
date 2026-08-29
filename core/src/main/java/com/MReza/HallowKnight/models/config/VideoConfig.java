package com.MReza.HallowKnight.models.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class VideoConfig {
    private static float brightness;
    private static Preferences prefs;

    public static void load() {
        prefs = Gdx.app.getPreferences("HollowKnight_Video");
        brightness = prefs.getFloat("brightness", 1.0f);
    }

    public static void save() {
        prefs.putFloat("brightness", brightness);
        prefs.flush();
    }

    public static float getBrightness() {
        return brightness;
    }

    public static void setBrightness(float brightness) {
        VideoConfig.brightness = brightness;
    }
}
