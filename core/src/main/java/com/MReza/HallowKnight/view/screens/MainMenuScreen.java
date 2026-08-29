package com.MReza.HallowKnight.view.screens;

import com.MReza.HallowKnight.Main;
import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.environment.BackGroundType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends AbstractScreen {

    public MainMenuScreen(Main game) {
        super(game, GameAssetManager.getBackground(BackGroundType.MAIN_MENU_BACKGROUND));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Texture logoTexture = new Texture(Gdx.files.internal("titles/vheart_title.png"));
        Image logoImage = new Image(logoTexture);

        TextButton startBtn = new TextButton("Start Game", hkButtonStyle);
        TextButton settingsBtn = new TextButton("Settings", hkButtonStyle);
        TextButton helpBtn = new TextButton("Help", hkButtonStyle);
        TextButton achievementsBtn = new TextButton("Achievements", hkButtonStyle);
        TextButton exitBtn = new TextButton("Exit", hkButtonStyle);

        startBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StartGameScreen(game));
            }
        });

        settingsBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, MainMenuScreen.this));
            }
        });

        helpBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HelpScreen(game));
            }
        });

        achievementsBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new AchievementsScreen(game));
            }
        });

        exitBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(logoImage).row();

        table.add(startBtn).fillX().pad(8).width(200).row();
        table.add(settingsBtn).fillX().pad(8).width(200).row();
        table.add(achievementsBtn).fillX().pad(8).width(200).row();
        table.add(helpBtn).fillX().pad(8).width(200).row();
        table.add(exitBtn).fillX().pad(8).width(200).row();
    }


    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

}
