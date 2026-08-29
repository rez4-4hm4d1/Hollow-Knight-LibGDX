package com.MReza.HallowKnight.view.screens;

import com.MReza.HallowKnight.Main;
import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.config.AudioConfig;
import com.MReza.HallowKnight.models.config.VideoConfig;
import com.MReza.HallowKnight.models.environment.MusicType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AbstractScreen implements Screen {
    protected Main game;
    protected Stage stage;
    protected Skin skin;
    protected TextButton.TextButtonStyle hkButtonStyle;
    protected Texture bgTexture;
    protected SpriteBatch batch;
    protected ShapeRenderer shapeRenderer;

    public AbstractScreen(Main game, Texture bgTexture){
        this.game = game;
        stage = new Stage(new FitViewport(1280, 720));
        this.bgTexture = bgTexture;
        skin = GameAssetManager.getSkin();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        hkButtonStyle = GameAssetManager.getHKButtonStyle();

    }

    public void renderBackground() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(bgTexture != null){
            batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
            batch.begin();
            batch.draw(bgTexture, 0, 0, 1280, 720);
            batch.end();
        }
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        playScreenMusic();
    }

    protected void playScreenMusic() {
        GameAssetManager.playMusic(GameAssetManager.getMenuMusic(MusicType.MENU_MUSIC));
    }

    @Override
    public void render(float delta) {
        renderBackground();

        GameAssetManager.updateFading(delta);

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();

        drawBrightnessLayer();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        if(skin != null) skin.dispose();
        if(bgTexture != null) bgTexture.dispose();

        if(batch != null) batch.dispose();
    }
    public void drawBrightnessLayer(){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float alpha = 1.0f - VideoConfig.getBrightness();
        shapeRenderer.setColor(0, 0, 0, alpha);

        shapeRenderer.rect(0, 0, 1280, 720);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
