package com.MReza.HallowKnight.view.screens;

import com.MReza.HallowKnight.Main;
import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.controllers.ProfileManager;
import com.MReza.HallowKnight.models.environment.BackGroundType;
import com.MReza.HallowKnight.models.player.Achievement;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class AchievementsScreen extends AbstractScreen {

    private List<Texture> achievementIcons;

    public AchievementsScreen(Main game) {
        super(game, GameAssetManager.getBackground(BackGroundType.ACHIEVEMENTS_BACKGROUND));

        achievementIcons = new ArrayList<>();

        Table containerTable = new Table();
        containerTable.setFillParent(true);
        stage.addActor(containerTable);

        Table contentTable = new Table();
        contentTable.align(Align.top);

        Label titleLabel = new Label("--- ACHIEVEMENTS ---", GameAssetManager.getColoredLabelStyle(Color.GOLD));
        titleLabel.setFontScale(0.6f);
        contentTable.add(titleLabel).padBottom(30).padTop(20).center().expandX().fillX().row();

        ProfileManager.loadProfile();

        for (Achievement ach : Achievement.values()) {
            boolean isUnlocked = ProfileManager.isAchievementUnlocked(ach.name());

            addAchievement(contentTable, ach.getTitle(), ach.getDescription(), isUnlocked, ach.getIconPath());
        }

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(false, true);

        TextButton backBtn = new TextButton("Back", hkButtonStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameAssetManager.playClickSound();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        containerTable.add(scrollPane).width(900).expandY().fillY().padTop(50).padBottom(20).row();
        containerTable.add(backBtn).padBottom(30).center();
    }

    private void addAchievement(Table table, String title, String desc, boolean isUnlocked, String iconPath) {
        Table rowTable = new Table();
        rowTable.align(Align.left);

        Texture iconTexture;
        iconTexture = new Texture(Gdx.files.internal(iconPath));

        achievementIcons.add(iconTexture);
        Image iconImage = new Image(iconTexture);

        Label nameLabel = new Label(title, GameAssetManager.getColoredLabelStyle(Color.WHITE));
        Label descLabel = new Label(desc, GameAssetManager.getColoredLabelStyle(Color.WHITE));

        nameLabel.setFontScale(0.6f);
        descLabel.setFontScale(0.3f);
        descLabel.setWrap(true);

        if (isUnlocked) {
            nameLabel.setColor(Color.GOLD);
            descLabel.setColor(Color.LIGHT_GRAY);
            iconImage.setColor(Color.WHITE);
        } else {
            nameLabel.setText("[LOCKED] " + title);
            nameLabel.setColor(Color.GRAY);
            descLabel.setText("Keep playing to unlock this achievement.");
            descLabel.setColor(Color.DARK_GRAY);
            iconImage.setColor(Color.DARK_GRAY);
        }

        rowTable.add(iconImage).size(80, 80).padRight(20).align(Align.center);

        Table textTable = new Table();
        textTable.add(nameLabel).align(Align.left).padBottom(5).row();
        textTable.add(descLabel).align(Align.left).expandX().fillX();

        rowTable.add(textTable).expandX().fillX().align(Align.center);
        table.add(rowTable).expandX().fillX().padBottom(30).row();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Texture tex : achievementIcons) {
            tex.dispose();
        }
    }
}
