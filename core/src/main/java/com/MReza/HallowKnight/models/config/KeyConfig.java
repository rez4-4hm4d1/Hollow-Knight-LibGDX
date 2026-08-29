package com.MReza.HallowKnight.models.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

public class KeyConfig {
    public static int INVENTORY;
    public static int MOVE_LEFT;
    public static int MOVE_RIGHT;
    public static int JUMP;
    public static int ATTACK;
    public static int DASH;
    public static int FOCUS;

    public static void load(){
        Preferences prefs = Gdx.app.getPreferences("HollowKnight_KeyBinds");

        MOVE_LEFT = prefs.getInteger("left", Input.Keys.LEFT);
        MOVE_RIGHT = prefs.getInteger("right", Input.Keys.RIGHT);
        JUMP = prefs.getInteger("jump", Input.Keys.Z);
        ATTACK = prefs.getInteger("attack", Input.Keys.X);
        DASH = prefs.getInteger("dash", Input.Keys.C);
        INVENTORY = prefs.getInteger("inventory", Input.Keys.I);
        FOCUS = prefs.getInteger("focus", Input.Keys.A);

    }

    public static boolean isKeyUsedByAnotherAction(String currentAction, int keycode) {
        if (keycode == MOVE_LEFT && !currentAction.equals("left")) return true;
        if (keycode == MOVE_RIGHT && !currentAction.equals("right")) return true;
        if (keycode == JUMP && !currentAction.equals("jump")) return true;
        if (keycode == ATTACK && !currentAction.equals("attack")) return true;
        if (keycode == DASH && !currentAction.equals("dash")) return true;
        if (keycode == INVENTORY && !currentAction.equals("inventory")) return  true;
        if (keycode == FOCUS && !currentAction.equals("focus")) return true;
        return false;
    }

    public static void resetToDefaults() {
        Preferences prefs = Gdx.app.getPreferences("HollowKnight_KeyBinds");
        prefs.putInteger("left", Input.Keys.LEFT);
        prefs.putInteger("right", Input.Keys.RIGHT);
        prefs.putInteger("jump", Input.Keys.Z);
        prefs.putInteger("attack", Input.Keys.X);
        prefs.putInteger("dash", Input.Keys.C);
        prefs.putInteger("inventory", Input.Keys.I);
        prefs.putInteger("focus", Input.Keys.A);
        prefs.flush();
        load();
    }
    public static void saveKey(String action, int keycode){
        Preferences prefs = Gdx.app.getPreferences("HollowKnight_KeyBinds");
        prefs.putInteger(action, keycode);
        prefs.flush();
        load();
    }
}
