package com.MReza.HallowKnight.controllers;

import com.MReza.HallowKnight.models.AnimationType;
import com.MReza.HallowKnight.models.SoundType;
import com.MReza.HallowKnight.models.enemies.EnemyAnimationType;
import com.MReza.HallowKnight.models.enemies.EnemySoundType;
import com.MReza.HallowKnight.models.environment.*;
import com.MReza.HallowKnight.models.particle.ParticleAnimationType;
import com.MReza.HallowKnight.models.player.CharmType;
import com.MReza.HallowKnight.models.player.PlayerAnimationType;
import com.MReza.HallowKnight.models.config.AudioConfig;
import com.MReza.HallowKnight.models.config.KeyConfig;
import com.MReza.HallowKnight.models.config.VideoConfig;
import com.MReza.HallowKnight.models.player.PlayerSoundType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameAssetManager {
    private static Skin skin;
    private static TextButton.TextButtonStyle hkButtonStyle;
    private static TextButton.TextButtonStyle hkButtonStyleSmall;
    private static Cursor customCursor;

    private static final Map<PlayerAnimationType, Animation<TextureRegion>> playerAnimations = new HashMap<>();
    private static final Map<EnemyAnimationType, Animation<TextureRegion>> enemiesAnimations = new HashMap<>();
    private static final Map<PlayerSoundType, List<Sound>> playerSounds = new HashMap<>();
    private static final Map<EnemySoundType, List<Sound>> enemiesSound = new HashMap<>();
    private static final Map<MapType, TiledMap> maps = new HashMap<>();
    private static final Map<BackGroundType, Texture> backgrounds = new HashMap<>();
    private static final Map<MusicType, Music> musics = new HashMap<>();
    private static final Map<UiType, TextureRegion> uiElements = new HashMap<>();
    private static final Map<CharmType, TextureRegion> charms = new HashMap<>();
    private static final Map<FontType, BitmapFont> fonts = new HashMap<>();
    private static final Map<ParticleAnimationType, Animation<TextureRegion>> particleAnimations = new HashMap<>();
    private static Sound clickSound;
    private static Sound notifSound;
    private static Music currentMusic;

    private static Music fadingOutMusic;
    private static Music fadingInMusic;
    private static float fadeDuration = 1.5f;
    private static float fadeTimer = 0f;
    private static boolean isFading = false;


    public static void init(){
        skin = new Skin(Gdx.files.internal("ui/skin/comic-ui.json"));

        for (FontType type : FontType.values()){
            loadFont(type);
        }

        for (PlayerAnimationType type : PlayerAnimationType.values()){
            playerAnimations.put(type, createAnimation(type));
        }
        for (EnemyAnimationType type : EnemyAnimationType.values()){
            enemiesAnimations.put(type, createAnimation(type));
        }
        for (ParticleAnimationType type : ParticleAnimationType.values()){
            particleAnimations.put(type, createAnimation(type));
        }
        for (PlayerSoundType type : PlayerSoundType.values()){
            playerSounds.put(type, createSound(type));
        }
        for (EnemySoundType type : EnemySoundType.values()){
            enemiesSound.put(type, createSound(type));
        }
        for (BackGroundType type : BackGroundType.values()){
            loadBackground(type);
        }
        for (MapType type : MapType.values()){
            loadMap(type);
        }
        for (MusicType type : MusicType.values()){
            loadMusic(type);
        }
        for (UiType type : UiType.values()){
            loadUi(type);
        }
        for (CharmType type : CharmType.values()){
            loadCharm(type);
        }

        AudioConfig.load();
        VideoConfig.load();
        KeyConfig.load();


        currentMusic = getMenuMusic(MusicType.MENU_MUSIC);
        clickSound = Gdx.audio.newSound(Gdx.files.internal("audio/side_sounds/button.wav"));
        notifSound = Gdx.audio.newSound(Gdx.files.internal("audio/side_sounds/notif.wav"));


        currentMusic.setLooping(true);

        initCursor();
    }
    public static void playMusic(Music newMusic) {
        if (currentMusic == newMusic && currentMusic != null && currentMusic.isPlaying()) {
            return;
        }
        if (currentMusic != null && currentMusic.isPlaying()) {
            fadingOutMusic = currentMusic;
        }

        fadingInMusic = newMusic;
        fadeTimer = 0f;
        isFading = true;

        if (fadingInMusic != null && AudioConfig.isMusicOn()) {
            fadingInMusic.setVolume(0f);
            fadingInMusic.setLooping(true);
            fadingInMusic.play();
        }

        currentMusic = newMusic;
    }
    public static void updateFading(float delta) {
        if (!isFading) return;

        fadeTimer += delta;
        float progress = Math.min(fadeTimer / fadeDuration, 1.0f);

        float targetVolume = AudioConfig.isMusicOn() ? AudioConfig.getMusicVolume() : 0f;

        if (fadingOutMusic != null) {
            fadingOutMusic.setVolume(targetVolume * (1f - progress));
        }

        if (fadingInMusic != null) {
            fadingInMusic.setVolume(targetVolume * progress);
        }

        if (progress >= 1.0f) {
            isFading = false;
            if (fadingOutMusic != null) {
                fadingOutMusic.stop();
                fadingOutMusic = null;
            }
        }
    }
    public static void updateMusicState() {
        if (currentMusic == null) return;

        if (AudioConfig.isMusicOn()) {
            if(!isFading){
                currentMusic.setVolume(AudioConfig.getMusicVolume());
            }
            if (!currentMusic.isPlaying()) {
                currentMusic.play();
            }
        }
        else {
            currentMusic.pause();
            if (fadingOutMusic != null){
                fadingOutMusic.pause();
            }
        }
    }

    public static void playClickSound() {
        if (AudioConfig.isSfxOn()) {
            clickSound.play(1.0f);
        }
    }
    public static void playNotifSound() {
        if (AudioConfig.isSfxOn()) {
            notifSound.play(1.0f);
        }
    }
    private static Animation<TextureRegion> createAnimation(AnimationType type){
        Texture sheet = new Texture(Gdx.files.internal(type.getPath()));

        int frameWidth = sheet.getWidth() / type.getFrameCount();
        int frameHeight = sheet.getHeight();

        TextureRegion[][] temp = TextureRegion.split(sheet, frameWidth, frameHeight);

        TextureRegion[] frames = new TextureRegion[type.getFrameCount()];
        for (int i = 0; i < type.getFrameCount(); i ++){
            frames[i] = temp[0][i];
        }
        return new Animation<>(type.getDuration(), frames);
    }
    private static List<Sound> createSound(SoundType type){
        List<Sound> sounds = new ArrayList<>();
        for (String path : type.getPaths()){
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sounds.add(sound);
        }
        return sounds;
    }
    private static void loadMap(MapType type){
        String path = type.getPath();
        TiledMap map = new TmxMapLoader().load(path);
        maps.put(type, map);
    }
    private static void loadBackground(BackGroundType type){
        String path = type.getPath();
        Texture bg = new Texture(Gdx.files.internal(path));
        backgrounds.put(type, bg);
    }
    private static void loadMusic(MusicType type){
        String path = type.getPath();
        Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
        musics .put(type, music);
    }
    private static void loadUi(UiType type){
        String path = type.getPath();
        TextureRegion uiElement = new TextureRegion(new Texture(Gdx.files.internal(path)));
        uiElements.put(type, uiElement);
    }
    private static void loadCharm(CharmType type){
        String path = type.getPath();
        TextureRegion charm = new TextureRegion(new Texture(Gdx.files.internal(path)));
        charms.put(type, charm);
    }
    public static void initCursor() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("ui/cursor/cursor.png"));
        int xHotspot = 0;
        int yHotspot = 0;

        customCursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        Gdx.graphics.setCursor(customCursor);

        pixmap.dispose();
    }
    private static void loadFont(FontType type){
        BitmapFont font = new BitmapFont(Gdx.files.internal(type.getPath()));
        font.getData().setScale(type.getDefaultScale());
        fonts.put(type, font);
    }
    public static Skin getSkin() {
        return skin;
    }
    public static Animation<TextureRegion> getPlayerAnimation(PlayerAnimationType type) {
        return playerAnimations.get(type);
    }
    public static Animation<TextureRegion> getEnemyAnimation(EnemyAnimationType type){
        return enemiesAnimations.get(type);
    }
    public static Animation<TextureRegion> getParticleAnimation(ParticleAnimationType type) {
        return particleAnimations.get(type);
    }

    public static List<Sound> getPlayerSound(PlayerSoundType type){
        return playerSounds.get(type);
    }

    public static List<Sound> getEnemySound(EnemySoundType type){
        return enemiesSound.get(type);
    }

    public static Texture getBackground(BackGroundType type) {
        return backgrounds.get(type);
    }

    public static TiledMap getMap(MapType type){
        return maps.get(type);
    }

    public static Music getMenuMusic(MusicType type){
        return musics.get(type);
    }

    public static TextureRegion getUiElement(UiType type){
        return uiElements.get(type);
    }

    public static TextureRegion getCharm(CharmType type){return charms.get(type);}

    public static BitmapFont getFont(FontType type) {
        return fonts.get(type);
    }

    public static TextButton.TextButtonStyle getHKButtonStyle() {
        if (hkButtonStyle == null) {
            hkButtonStyle = new TextButton.TextButtonStyle();
            hkButtonStyle.font = getFont(FontType.HK_REGULAR);
            hkButtonStyle.fontColor = Color.WHITE;
            hkButtonStyle.overFontColor = Color.LIGHT_GRAY;
            hkButtonStyle.downFontColor = Color.DARK_GRAY;
        }
        return hkButtonStyle;
    }
    public static TextButton.TextButtonStyle getHkButtonStyleSmall() {
        if (hkButtonStyleSmall == null) {
            hkButtonStyleSmall = getHKButtonStyle();
            hkButtonStyleSmall.font = getFont(FontType.HK_MORE_SMALL);
        }
        return hkButtonStyleSmall;
    }
    public static Label.LabelStyle getHKLabelStyle() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getFont(FontType.HK_REGULAR);
        style.fontColor = Color.WHITE;
        return style;
    }

    public static Label.LabelStyle getColoredLabelStyle(Color color) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = GameAssetManager.getFont(FontType.HK_REGULAR);
        style.fontColor = color;
        return style;
    }
    public static Label.LabelStyle getColoredLabelStyleSmall(Color color) {
        Label.LabelStyle style = getColoredLabelStyle(color);
        style.font = GameAssetManager.getFont(FontType.HK_SMALL);
        return style;
    }
    public static Label.LabelStyle getColoredLabelStyleMoreSmall(Color color) {
        Label.LabelStyle style = getColoredLabelStyle(color);
        style.font = GameAssetManager.getFont(FontType.HK_MORE_SMALL);
        return style;
    }

    public static CheckBox.CheckBoxStyle getHKCheckBoxStyle() {
        CheckBox.CheckBoxStyle cbStyle = new CheckBox.CheckBoxStyle(skin.get(CheckBox.CheckBoxStyle.class));
        cbStyle.font = getFont(FontType.HK_REGULAR);
        cbStyle.fontColor = Color.WHITE;
        return cbStyle;
    }
}
