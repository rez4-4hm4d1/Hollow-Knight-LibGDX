package com.MReza.HallowKnight.view.screens;

import com.MReza.HallowKnight.Main;
import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.config.GameConfig;
import com.MReza.HallowKnight.models.environment.BackGroundType;
import com.MReza.HallowKnight.models.environment.MapType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class StartGameScreen extends AbstractScreen {

    public StartGameScreen(Main game) {
        super(game, GameAssetManager.getBackground(BackGroundType.START_GAME_BACKGROUND));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("SELECT GAME", GameAssetManager.getColoredLabelStyle(Color.BLACK));
        title.setFontScale(0.7f);
        table.add(title).padBottom(30).row();

        buildSlotUI(table, 1, MapType.FORGOTTEN_CROSSROADS);
        buildSlotUI(table, 2, MapType.FORGOTTEN_CROSSROADS);
        buildSlotUI(table, 3, MapType.GREEN_PATH);
        buildSlotUI(table, 4, MapType.GREEN_PATH);

        TextButton backBtn = new TextButton("Back", hkButtonStyle);
        backBtn.setColor(Color.BLACK);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameAssetManager.playClickSound();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        table.add(backBtn).fillX().colspan(2);
    }

    private void buildSlotUI(Table mainTable, int slotNumber, MapType defaultMap) {
        Table slotTable = new Table();

        Texture bannerTex = null;
        if (defaultMap == MapType.FORGOTTEN_CROSSROADS){
            bannerTex = GameAssetManager.getBackground(BackGroundType.FORGOTTEN_CROSSROADS_BANNER);
        }
        else {
            bannerTex = GameAssetManager.getBackground(BackGroundType.GREEN_PATH_BANNER);
        }
        slotTable.setBackground(new TextureRegionDrawable(new TextureRegion(bannerTex)));
        slotTable.pad(20);

        if (GameConfig.hasSave(slotNumber)) {
            final MapType mapToLoad = GameConfig.loadMap(slotNumber);

            TextButton continueBtn = new TextButton("[Continue]", hkButtonStyle);
            continueBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameAssetManager.playClickSound();
                    game.setScreen(new GameScreen(game, mapToLoad, slotNumber, false));
                }
            });

            TextButton overwriteBtn = new TextButton("[New Game]", hkButtonStyle);
            overwriteBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameAssetManager.playClickSound();
                    game.setScreen(new GameScreen(game, defaultMap, slotNumber, true));
                }
            });

            slotTable.add(continueBtn).fillX().padRight(15);
            slotTable.add(overwriteBtn).fillX();

        } else {
            // (isNewGame = true)
            TextButton newBtn = new TextButton("[New Game]", hkButtonStyle);
            newBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameAssetManager.playClickSound();
                    game.setScreen(new GameScreen(game, defaultMap, slotNumber, true));
                }
            });
            slotTable.add(newBtn).fillX();
        }

        mainTable.add(slotTable).width(600).height(120).padBottom(15).row();
    }
}
