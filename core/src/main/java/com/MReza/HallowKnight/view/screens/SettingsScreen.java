package com.MReza.HallowKnight.view.screens;

import com.MReza.HallowKnight.Main;
import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.config.AudioConfig;
import com.MReza.HallowKnight.models.config.KeyConfig;
import com.MReza.HallowKnight.models.config.VideoConfig;
import com.MReza.HallowKnight.models.environment.BackGroundType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.rmi.dgc.VMID;

import static com.badlogic.gdx.utils.Align.left;

public class SettingsScreen extends AbstractScreen {
    private Screen previousScreen;

    // for changing key logic
    private boolean isWaitingForKey = false;
    private String actionToBind = "";
    private TextButton currentBindingButton = null;

    private Table mainTable;
    private Table keysTable;

    public SettingsScreen(Main game, Screen previousScreen) {
        super(game, GameAssetManager.getBackground(BackGroundType.SETTINGS_BACKGROUND));
        this.previousScreen = previousScreen;

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        keysTable = new Table();
        keysTable.setFillParent(true);
        keysTable.setVisible(false);
        stage.addActor(keysTable);

        setupMainTable();
        setupKeysTable();

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (isWaitingForKey) {
                    if (keycode != Input.Keys.UNKNOWN) {

                        if (keycode == Input.Keys.ESCAPE) {
                            currentBindingButton.setText("RESERVED KEY! TRY AGAIN...");
                            currentBindingButton.getLabel().setColor(Color.RED);
                            return true;
                        }

                        if (KeyConfig.isKeyUsedByAnotherAction(actionToBind, keycode)) {
                            currentBindingButton.setText("ALREADY USED! TRY AGAIN...");
                            currentBindingButton.getLabel().setColor(Color.RED);
                            return true;
                        }

                        KeyConfig.saveKey(actionToBind, keycode);
                        GameAssetManager.playClickSound();

                        currentBindingButton.setText(actionToBind.toUpperCase() + ": " + Input.Keys.toString(keycode));

                        isWaitingForKey = false;
                        currentBindingButton.getLabel().setColor(Color.WHITE);
                    }
                    return true; // success
                }
                return super.keyDown(event, keycode);
            }
        });
    }

    private void setupMainTable() {
        CheckBox.CheckBoxStyle cbStyle = GameAssetManager.getHKCheckBoxStyle();

        CheckBox musicToggle = new CheckBox(" Music On/Off", cbStyle);
        CheckBox sfxToggle = new CheckBox(" SFX On/Off", cbStyle);

        Label musicVolLabel = new Label("Music Volume", GameAssetManager.getHKLabelStyle());
        Slider musicSlider = new Slider(0f, 1f, 0.05f, false, skin);

        musicSlider.setColor(Color.LIGHT_GRAY);
        musicSlider.setValue(AudioConfig.getMusicVolume());
        musicToggle.setChecked(AudioConfig.isMusicOn());
        sfxToggle.setChecked(AudioConfig.isSfxOn());

        Label brightnessLabel = new Label("Brightness", GameAssetManager.getHKLabelStyle());
        Slider brightnessSlider = new Slider(0.2f, 1f, 0.1f, false, skin);
        brightnessSlider.setColor(Color.LIGHT_GRAY);
        brightnessSlider.setValue(VideoConfig.getBrightness());

        TextButton resetAudioBtn = new TextButton("Reset Audio", hkButtonStyle);
        resetAudioBtn.getLabel().setColor(Color.ORANGE);
        TextButton changeKeysBtn = new TextButton("Change Controls", hkButtonStyle);
        changeKeysBtn.getLabel().setColor(Color.ORANGE);
//        TextButton languageBtn = new TextButton("Language: English", hkButtonStyle);
        TextButton backBtn = new TextButton("Back", hkButtonStyle);

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioConfig.setMusicVolume(musicSlider.getValue());
                GameAssetManager.updateMusicState();
                AudioConfig.save();
            }
        });

        sfxToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioConfig.setIsSfxOn(sfxToggle.isChecked());
                GameAssetManager.playClickSound();
                AudioConfig.save();
            }
        });

        musicToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioConfig.setIsMusicOn(musicToggle.isChecked());
                GameAssetManager.updateMusicState();
                AudioConfig.save();
            }
        });

        brightnessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                VideoConfig.setBrightness(brightnessSlider.getValue());
                VideoConfig.save();
            }
        });
        resetAudioBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {

                AudioConfig.setMusicVolume(0.5f);
                AudioConfig.setIsMusicOn(true);
                AudioConfig.setIsSfxOn(true);;
                AudioConfig.save();

                musicSlider.setValue(0.5f);
                musicToggle.setChecked(true);
                sfxToggle.setChecked(true);

                GameAssetManager.updateMusicState();
            }
        });

        changeKeysBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameAssetManager.playClickSound();
                mainTable.setVisible(false);
                keysTable.setVisible(true);
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameAssetManager.playClickSound();
                game.setScreen(previousScreen);
            }
        });

        mainTable.add(musicVolLabel).pad(5);
        mainTable.add(musicSlider).width(200).pad(5).row();
        mainTable.add(brightnessLabel).pad(5);
        mainTable.add(brightnessSlider).width(200).pad(5).row();
        mainTable.add(musicToggle).pad(5);
        mainTable.add(resetAudioBtn).pad(5).row();
        mainTable.add(sfxToggle).pad(5).row();
        mainTable.add(changeKeysBtn).pad(10).row();
//        mainTable.add(languageBtn).pad(10).row();
        mainTable.add(backBtn).padTop(20).align(left);
    }

    private void setupKeysTable() {
        Label title = new Label("Press a button below to change its key", GameAssetManager.getColoredLabelStyle(Color.ORANGE));

        TextButton jumpBtn = new TextButton("JUMP: " + Input.Keys.toString(KeyConfig.JUMP), hkButtonStyle);
        TextButton attackBtn = new TextButton("ATTACK: " + Input.Keys.toString(KeyConfig.ATTACK), hkButtonStyle);
        TextButton dashBtn = new TextButton("DASH: " + Input.Keys.toString(KeyConfig.DASH), hkButtonStyle);
        TextButton leftBtn = new TextButton("LEFT: " + Input.Keys.toString(KeyConfig.MOVE_LEFT), hkButtonStyle);
        TextButton rightBtn = new TextButton("RIGHT: " + Input.Keys.toString(KeyConfig.MOVE_RIGHT), hkButtonStyle);
        TextButton focusBtn = new TextButton("FOCUS: " + Input.Keys.toString(KeyConfig.FOCUS), hkButtonStyle);
        TextButton inventoryBtn = new TextButton("INVENTORY: " + Input.Keys.toString(KeyConfig.INVENTORY), hkButtonStyle);

        TextButton resetKeysBtn = new TextButton("Reset To Defaults", hkButtonStyle);
        resetKeysBtn.getLabel().setColor(Color.ORANGE);

        TextButton backToMainBtn = new TextButton("Back", hkButtonStyle);

        addBindListener(jumpBtn, "jump");
        addBindListener(attackBtn, "attack");
        addBindListener(dashBtn, "dash");
        addBindListener(leftBtn, "left");
        addBindListener(rightBtn, "right");
        addBindListener(focusBtn, "focus");
        addBindListener(inventoryBtn, "inventory");

        resetKeysBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameAssetManager.playClickSound();
                KeyConfig.resetToDefaults();

                jumpBtn.setText("JUMP: " + Input.Keys.toString(KeyConfig.JUMP));
                attackBtn.setText("ATTACK: " + Input.Keys.toString(KeyConfig.ATTACK));
                dashBtn.setText("DASH: " + Input.Keys.toString(KeyConfig.DASH));
                leftBtn.setText("LEFT: " + Input.Keys.toString(KeyConfig.MOVE_LEFT));
                rightBtn.setText("RIGHT: " + Input.Keys.toString(KeyConfig.MOVE_RIGHT));
                focusBtn.setText("FOCUS: " + Input.Keys.toString(KeyConfig.FOCUS));
                inventoryBtn.setText("INVENTORY: " + Input.Keys.toString(KeyConfig.INVENTORY));
            }
        });

        backToMainBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameAssetManager.playClickSound();

                if (isWaitingForKey && currentBindingButton != null) { //the condition of (currentBindingButton != null) is just for sure (it never happens in this code)
                    isWaitingForKey = false;
                    currentBindingButton.getLabel().setColor(Color.WHITE);

                    int actualKey = 0;
                    if (actionToBind.equals("jump")) actualKey = KeyConfig.JUMP;
                    else if (actionToBind.equals("attack")) actualKey = KeyConfig.ATTACK;
                    else if (actionToBind.equals("dash")) actualKey = KeyConfig.DASH;
                    else if (actionToBind.equals("left")) actualKey = KeyConfig.MOVE_LEFT;
                    else if (actionToBind.equals("right")) actualKey = KeyConfig.MOVE_RIGHT;
                    else if (actionToBind.equals("inventory")) actualKey = KeyConfig.INVENTORY;
                    else if (actionToBind.equals("focus")) actualKey = KeyConfig.FOCUS;

                    currentBindingButton.setText(actionToBind.toUpperCase() + ": " + Input.Keys.toString(actualKey));
                }
                keysTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });

        keysTable.add(title).padBottom(20).colspan(2).row();
        keysTable.add(jumpBtn).pad(5).fillX();
        keysTable.add(attackBtn).pad(5).fillX().row();
        keysTable.add(leftBtn).pad(5).fillX();
        keysTable.add(rightBtn).pad(5).fillX().row();
        keysTable.add(dashBtn).pad(5).fillX();
        keysTable.add(focusBtn).pad(5).fillX().row();
        keysTable.add(inventoryBtn).pad(5).fillX().row();
        keysTable.add(resetKeysBtn).padTop(20).colspan(2).row();
        keysTable.add(backToMainBtn).padTop(10).colspan(2);
    }

    private void addBindListener(TextButton button, String action) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isWaitingForKey) {
                    GameAssetManager.playClickSound();
                    isWaitingForKey = true;
                    actionToBind = action;
                    currentBindingButton = button;
                    button.setText("PRESS ANY KEY...");
                    button.getLabel().setColor(Color.YELLOW);
                }
            }
        });
    }

    @Override
    protected void playScreenMusic() {
        // this method should be empty
    }
}
