package com.MReza.HallowKnight.view.screens;

import com.MReza.HallowKnight.Main;
import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.config.KeyConfig;
import com.MReza.HallowKnight.models.environment.BackGroundType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class HelpScreen extends AbstractScreen {

    public HelpScreen(Main game) {
        super(game, GameAssetManager.getBackground(BackGroundType.HELP_BACKGROUND));

        // main table
        Table containerTable = new Table();
        containerTable.setFillParent(true);
        stage.addActor(containerTable);

        // inner table
        Table contentTable = new Table();
        contentTable.align(Align.top);
        contentTable.defaults().padBottom(10).align(Align.left);

        Label titleLabel = new Label(" GAME GUIDE ", GameAssetManager.getColoredLabelStyle(Color.WHITE));
        titleLabel.setFontScale(0.6f);
        contentTable.add(titleLabel).padBottom(40).padTop(20).center().expandX().fillX().row();

        Label sectionControls = new Label("> CONTROLS ", GameAssetManager.getColoredLabelStyle(Color.ORANGE));
        sectionControls.setFontScale(0.5f);
        contentTable.add(sectionControls).padBottom(20).row();

        Table controlsTable = new Table();
        controlsTable.align(Align.left);

        addControlRow(controlsTable, "Move Left / Right", "[" + Input.Keys.toString(KeyConfig.MOVE_LEFT) + "] / [" + Input.Keys.toString(KeyConfig.MOVE_RIGHT) + "]");
        addControlRow(controlsTable, "Jump Action", "[" + Input.Keys.toString(KeyConfig.JUMP) + "]");
        addControlRow(controlsTable, "Attack (Nail)", "[" + Input.Keys.toString(KeyConfig.ATTACK) + "]");
        addControlRow(controlsTable, "Dash", "[" + Input.Keys.toString(KeyConfig.DASH) + "]");
        addControlRow(controlsTable, "Focus Action",  "[" + Input.Keys.toString(KeyConfig.FOCUS) + "]");
        addControlRow(controlsTable, "Inventory Menu", "[" + Input.Keys.toString(KeyConfig.INVENTORY) + "]");

        contentTable.add(controlsTable).padBottom(40).expandX().fillX().row();

        Label sectionMechanics = new Label("> ABILITIES & MECHANICS ", GameAssetManager.getColoredLabelStyle(Color.ORANGE));
        sectionMechanics.setFontScale(0.5f);
        contentTable.add(sectionMechanics).padBottom(20).row();

        Table mechanicsTable = new Table();
        mechanicsTable.align(Align.left);

        addBulletPoint(mechanicsTable, "Health (Masks)", "You lose one mask whenever you take damage from enemies.");
        addBulletPoint(mechanicsTable, "Soul Vessel", "Striking enemies with your nail fills up your Soul meter.");
        addBulletPoint(mechanicsTable, "Focus Healing", "Hold [" + Input.Keys.toString(KeyConfig.FOCUS) + "] to spend Soul. This allows you to focus and heal damaged masks during safe moments.");

        contentTable.add(mechanicsTable).padBottom(40).expandX().fillX().row();

        Label sectionCheats = new Label("> CHEAT CODES ", GameAssetManager.getColoredLabelStyle(Color.ORANGE));
        sectionCheats.setFontScale(0.5f);
        contentTable.add(sectionCheats).padBottom(20).row();

        Table cheatsTable = new Table();
        cheatsTable.align(Align.left);

        addCheatRow(cheatsTable, "Ctrl + B", "Boss Arena Teleport: Teleport instantly to False Knight arena.");
        addCheatRow(cheatsTable, "Ctrl + N", "Noclip / Spectator: Free movement, no gravity, ignore obstacles.");
        addCheatRow(cheatsTable, "Ctrl + H", "Emergency Heal: Grants 1 extra mask when health is critical/empty.");
        addCheatRow(cheatsTable, "Ctrl + S", "Refill Soul Vessel: Instantly fills the soul meter.");
        addCheatRow(cheatsTable, "Ctrl + G", "God Mode: Invincibility against all enemies and hazards.");
        addCheatRow(cheatsTable, "Ctrl + K", "Insta-Kill (Bonus): Kills all current enemies on the screen instantly.");

        contentTable.add(cheatsTable).padBottom(40).expandX().fillX().row();

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(false, true);

        TextButton backBtn = new TextButton("Back", hkButtonStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        containerTable.add(scrollPane).width(900).expandY().fillY().padTop(50).padBottom(20).row();
        containerTable.add(backBtn).padBottom(30).center();
    }

    private void addControlRow(Table table, String actionName, String keyText) {
        Label actionLabel = new Label(actionName, GameAssetManager.getColoredLabelStyle(Color.WHITE));
        actionLabel.setFontScale(0.5f);
        Label keyLabel = new Label(keyText, GameAssetManager.getColoredLabelStyle(Color.ORANGE));
        keyLabel.setFontScale(0.5f);

        table.add(actionLabel).width(350).padBottom(15).align(Align.left);
        table.add(keyLabel).padBottom(15).align(Align.left).expandX().row();
    }

    private void addBulletPoint(Table table, String topic, String description) {
        Label dotLabel = new Label("> ", GameAssetManager.getColoredLabelStyle(Color.ORANGE));
        dotLabel.setFontScale(0.4f);

        Label topicLabel = new Label(topic + ": ", GameAssetManager.getColoredLabelStyle(Color.LIGHT_GRAY));
        topicLabel.setFontScale(0.4f);

        Label descLabel = new Label(description, GameAssetManager.getColoredLabelStyle(Color.WHITE));
        descLabel.setFontScale(0.3f);
        descLabel.setWrap(true);

        table.add(dotLabel).align(Align.topLeft).padBottom(15).padRight(10);
        table.add(topicLabel).align(Align.topLeft).width(250).padBottom(15);
        table.add(descLabel).align(Align.topLeft).expandX().fillX().padBottom(15).row();
    }

    private void addCheatRow(Table table, String cheatCode, String description) {
        Label codeLabel = new Label("\"" + cheatCode + "\"", GameAssetManager.getColoredLabelStyle(Color.ORANGE));
        codeLabel.setFontScale(0.4f);

        Label descLabel = new Label("- " + description, GameAssetManager.getColoredLabelStyle(Color.WHITE));
        descLabel.setFontScale(0.3f);
        descLabel.setWrap(true);

        table.add(codeLabel).width(200).align(Align.topLeft).padBottom(15);
        table.add(descLabel).align(Align.topLeft).expandX().fillX().padBottom(15).row();
    }
}
