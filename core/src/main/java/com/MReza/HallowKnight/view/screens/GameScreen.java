package com.MReza.HallowKnight.view.screens;

import com.MReza.HallowKnight.Main;
import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.controllers.GameController;
import com.MReza.HallowKnight.controllers.ProfileManager;
import com.MReza.HallowKnight.models.GameWorld;
import com.MReza.HallowKnight.models.config.GameConfig;
import com.MReza.HallowKnight.models.config.KeyConfig;
import com.MReza.HallowKnight.models.enemies.*;
import com.MReza.HallowKnight.models.environment.*;
import com.MReza.HallowKnight.models.particle.Particle;
import com.MReza.HallowKnight.models.particle.ParticleAnimationType;
import com.MReza.HallowKnight.models.particle.ParticleZone;
import com.MReza.HallowKnight.models.player.Achievement;
import com.MReza.HallowKnight.models.player.CharmType;
import com.MReza.HallowKnight.models.player.Player;
import com.MReza.HallowKnight.models.player.PlayerAnimationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.LinkedList;
import java.util.Queue;

public class GameScreen extends AbstractScreen {

    private GameWorld world;
    private GameController controller;

    private Table pauseMenu;
    private Table inventoryMenu;
    private Table victoryMenu;
    private Table achievementToast;
    private Label toastLabel;
    private Label statsLabel;
    private Label cheatsDisplay;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;

    private MapType currentMapType;
    private int currentSaveSlot;

    private float cameraShakeDuration = 0f;
    private float cameraShakeIntensity = 0f;

    // for toast notifs
    private String toastMessage = "";
    private float toastTimer = 0f;
    private float toastY = -100f;

    private Label charmDescLabel;
    private Label notchCountLabel;

    // for handling multi-notifs
    private Queue<Achievement> achievementQueue = new LinkedList<>();
    private boolean isToastShowing = false;

    private static BackGroundType getBackgroundForMap(MapType type) {
        return (type == MapType.GREEN_PATH) ? BackGroundType.GREEN_PATH_BG : BackGroundType.FORGOTTEN_CROSSROADS_BG;
    }

    public GameScreen(Main game, MapType mapType, int saveSlot, boolean isNewGame) {
        super(game, GameAssetManager.getBackground(getBackgroundForMap(mapType)));
        this.currentMapType = mapType;
        this.currentSaveSlot = saveSlot;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, 1280, 720);

        Player player = new Player(100, 200);
        if (!isNewGame) GameConfig.loadPlayerState(currentSaveSlot, player);

        world = new GameWorld(player);
        controller = new GameController(world, this);

        map = GameAssetManager.getMap(currentMapType);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        loadMapData(isNewGame);
        setupUI();
    }

    private void loadMapData(boolean isNewGame) {
        // map siz
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("main");
        world.setMapWidth(layer.getWidth() * layer.getTileWidth());
        world.setMapHeight(layer.getHeight() * layer.getTileHeight());

        // particles
        MapLayer pZoneLayer = map.getLayers().get("particleZones");
        if (pZoneLayer != null) {
            for (MapObject object : pZoneLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    String pType = object.getProperties().containsKey("particleType") ? object.getProperties().get("particleType").toString() : "leaf";
                    ParticleAnimationType type = pType.equals("butterfly") ? ParticleAnimationType.BUTTERFLY : ParticleAnimationType.LEAF_FALL;
                    world.getParticleZones().add(new ParticleZone(rect, type));
                }
            }
        }

        // solids & spawn points
        MapLayer collisionLayer = map.getLayers().get("solids");
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    if (object.getProperties().containsKey("type")) {
                        String type = object.getProperties().get("type").toString();
                        if (type.equals("respawn")) {
                            world.getSpawnPoints().add(rect);
                        } else if (type.equals("start")) {
                            world.getPlayer().setMapStart(rect.x, rect.y);
                            if (isNewGame) {
                                world.getPlayer().setPosition(rect.x, rect.y);
                                world.getPlayer().setRespawnPoint(rect.x, rect.y);
                            }
                        } else {
                            world.getCollisionRects().add(rect);
                        }
                    } else {
                        world.getCollisionRects().add(rect);
                    }
                }
            }
        }

        // boss fight room
        MapLayer arenaLayer = map.getLayers().get("bossArena");
        if (arenaLayer != null) {
            for (MapObject object : arenaLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    world.setBossArenaBounds(((RectangleMapObject) object).getRectangle());
                    world.setArenaLeftWall(new Rectangle(world.getBossArenaBounds().x - 50, world.getBossArenaBounds().y, 50, world.getBossArenaBounds().height));
                    world.setArenaRightWall(new Rectangle(world.getBossArenaBounds().x + world.getBossArenaBounds().width, world.getBossArenaBounds().y, 50, world.getBossArenaBounds().height));
                }
            }
        }

        // enemies
        MapLayer enemyLayer = map.getLayers().get("enemies");
        if (enemyLayer != null) {
            for (MapObject object : enemyLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    if (object.getProperties().containsKey("enemyType")) {
                        String enemyType = object.getProperties().get("enemyType").toString();
                        if (enemyType.equals("husk")) world.getEnemies().add(new HuskHornhead(rect.x, rect.y, world.getPlayer()));
                        else if (enemyType.equals("crystalG")) world.getEnemies().add(new CrystalGuardian(rect.x, rect.y, world.getPlayer()));
                        else if (enemyType.equals("mosscreep")) world.getEnemies().add(new Mosscreep(rect.x, rect.y, world.getPlayer()));
                        else if (enemyType.equals("mosquito")) world.getEnemies().add(new Mosquito(rect.x, rect.y, world.getPlayer()));
                        else if (enemyType.equals("falseKnight")) world.getEnemies().add(new FalseKnight(rect.x, rect.y, world.getPlayer(), this, world));
                    }
                }
            }
        }

        // hazards
        MapLayer hazardsLayer = map.getLayers().get("hazards");
        if (hazardsLayer != null) {
            for (MapObject object : hazardsLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    String type = (String) object.getProperties().get("type");
                    if ("spike".equals(type)) world.getSpikeRects().add(rect);
                    else if ("pit".equals(type)) world.getPitRects().add(rect);
                }
            }
        }
        if (!isNewGame) GameConfig.loadEnemyStates(currentSaveSlot, world.getEnemies());
    }

    private void setupUI() {
        Stack uiStack = new Stack();
        uiStack.setFillParent(true);

        TextButton.TextButtonStyle hkButtonStyle = GameAssetManager.getHKButtonStyle();

        pauseMenu = new Table();
        pauseMenu.setVisible(false);
        Label pauseTitle = new Label("PAUSED", GameAssetManager.getColoredLabelStyle(Color.ORANGE));
        TextButton resumeBtn = new TextButton("Resume", hkButtonStyle);
        TextButton settingsBtn = new TextButton("Settings", hkButtonStyle);
        TextButton saveExitBtn = new TextButton("Save & Exit", hkButtonStyle);
        cheatsDisplay = new Label("Active Cheats: None", GameAssetManager.getColoredLabelStyle(Color.ORANGE));

        resumeBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.isPaused = false;
                pauseMenu.setVisible(false);
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
                game.setScreen(new SettingsScreen(game, GameScreen.this));
            }
        });
        saveExitBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameConfig.saveGame(currentSaveSlot, currentMapType, world.getPlayer(), world.getEnemies());
                game.setScreen(new MainMenuScreen(game));
            }
        });

        pauseMenu.add(pauseTitle).pad(20).row();
        pauseMenu.add(resumeBtn).fillX().pad(5).row();
        pauseMenu.add(settingsBtn).fillX().pad(5).row();
        pauseMenu.add(saveExitBtn).fillX().pad(5).row();
        pauseMenu.add(cheatsDisplay).padTop(20);

        inventoryMenu = new Table();
        inventoryMenu.setVisible(false);
        inventoryMenu.setFillParent(true);
        rebuildInventoryMenu();
        uiStack.add(inventoryMenu);

        victoryMenu = new Table();
        victoryMenu.setVisible(false);
        victoryMenu.center();
        Label victoryTitle = new Label("VICTORY ACHIEVED", GameAssetManager.getColoredLabelStyle(Color.YELLOW));
        statsLabel = new Label("", GameAssetManager.getColoredLabelStyle(Color.WHITE));
        TextButton restartBtn = new TextButton("Restart Run", hkButtonStyle);
        TextButton mainMenuBtn = new TextButton("Return to Menu", hkButtonStyle);

        restartBtn.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssetManager.playClickSound();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, currentMapType, currentSaveSlot, true));
            }
        });
        mainMenuBtn.addListener(new ClickListener() {
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

        victoryMenu.add(victoryTitle).padBottom(30).row();
        victoryMenu.add(statsLabel).padBottom(40).row();
        victoryMenu.add(restartBtn).fillX().pad(10).row();
        victoryMenu.add(mainMenuBtn).fillX().pad(10).row();

        uiStack.add(pauseMenu);
        uiStack.add(inventoryMenu);
        uiStack.add(victoryMenu);
        stage.addActor(uiStack);


        achievementToast = new Table();
        TextureRegion bgRegion = GameAssetManager.getUiElement(UiType.ACHIEVEMENT_BG);
        achievementToast.setBackground(new TextureRegionDrawable(bgRegion));

        Color toastColor = achievementToast.getColor();
        toastColor.a = 0.8f; // Transparency
        achievementToast.setColor(toastColor);

        achievementToast.setSize(500, 200);

        achievementToast.setPosition(760, -220);

        toastLabel = new Label("", GameAssetManager.getColoredLabelStyleSmall(Color.BLACK));
        toastLabel.setAlignment(Align.center);
        achievementToast.add(toastLabel).expand().fill();

        stage.addActor(achievementToast);
    }
    private void rebuildInventoryMenu() {
        inventoryMenu.clear();
        inventoryMenu.setVisible(false);

        int activeCount = ProfileManager.getData().equippedCharms.size();

        Label invTitle = new Label("Charms Inventory", GameAssetManager.getColoredLabelStyle(Color.ORANGE));
        notchCountLabel = new Label("Notches Used: " + activeCount + " / 3", GameAssetManager.getColoredLabelStyle(Color.WHITE));
        charmDescLabel = new Label("Select a charm to see details...", GameAssetManager.getColoredLabelStyle(Color.LIGHT_GRAY));
        charmDescLabel.setWrap(true);
        charmDescLabel.setAlignment(Align.center);

        Table grid = new Table();
        grid.pad(20);

        int col = 0;
        for (final CharmType charm : CharmType.values()) {
            boolean isEquipped = ProfileManager.isCharmEquipped(charm.name());

            Table charmContainer = new Table();
            charmContainer.setTouchable(Touchable.enabled);

            TextureRegion iconRegion = GameAssetManager.getCharm(charm);
            Image charmImage = new Image(iconRegion);

            String labelText = charm.getTitle();
            Label nameLabel = new Label(labelText, GameAssetManager.getColoredLabelStyleMoreSmall(Color.WHITE));
            nameLabel.setAlignment(Align.center);

            if (isEquipped) {
                charmImage.setColor(Color.WHITE);
                nameLabel.setColor(Color.ORANGE);
                nameLabel.setText(labelText + "\n(ON)");
            } else {
                charmImage.setColor(Color.DARK_GRAY);
                nameLabel.setColor(Color.GRAY);
            }

            charmContainer.add(charmImage).size(80, 80).padBottom(10).row();
            charmContainer.add(nameLabel).center();

            charmContainer.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                    charmDescLabel.setText(charm.getTitle() + ":\n" + charm.getDescription());
                }
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    GameAssetManager.playClickSound();
                    return super.touchDown(event, x, y, pointer, button);
                }
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    boolean success = ProfileManager.toggleCharm(charm.name());

                    if (!success && !ProfileManager.isCharmEquipped(charm.name())) {
                        charmDescLabel.setText("Not enough Notches! Max 3 charms allowed.");
                    } else {

                        if (ProfileManager.isCharmEquipped(charm.name())) {
                            triggerAchievementNotification(Achievement.CHARM_COLLECTOR);
                        }

                        charmDescLabel.setColor(Color.LIGHT_GRAY);
                        boolean wasVisible = inventoryMenu.isVisible();
                        rebuildInventoryMenu();
                        inventoryMenu.setVisible(wasVisible);
                    }
                }
            });

            grid.add(charmContainer).pad(15).width(160).height(120);
            col++;
            if (col == 4) {
                grid.row();
                col = 0;
            }
        }

        inventoryMenu.add(invTitle).padBottom(10).row();
        inventoryMenu.add(notchCountLabel).padBottom(20).row();
        inventoryMenu.add(grid).row();
        inventoryMenu.add(charmDescLabel).width(600).padTop(20);
    }

    @Override
    protected void playScreenMusic() {
        if (currentMapType == MapType.FORGOTTEN_CROSSROADS) {
            GameAssetManager.playMusic(GameAssetManager.getMenuMusic(MusicType.FORGOTTEN_CROSSROADS_MUSIC));
        } else if (currentMapType == MapType.GREEN_PATH) {
            GameAssetManager.playMusic(GameAssetManager.getMenuMusic(MusicType.GREEN_PATH_MUSIC));
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        GameAssetManager.updateFading(delta);

        handleUIInputs();
        controller.update(delta, camera);
        updateCamera(delta);
        drawGame(delta);
        drawUI(delta);
    }

    private void handleUIInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (controller.isInventoryOpen) {
                controller.isInventoryOpen = false;
                controller.isPaused = false;
                inventoryMenu.setVisible(false);
            } else {
                controller.isPaused = !controller.isPaused;
                pauseMenu.setVisible(controller.isPaused);
            }
        }

        if (Gdx.input.isKeyJustPressed(KeyConfig.INVENTORY)) {
            if (controller.isInventoryOpen) {
                controller.isInventoryOpen = false;
                controller.isPaused = false;
                inventoryMenu.setVisible(false);
            } else if (!controller.isPaused) {
                controller.isInventoryOpen = true;
                controller.isPaused = true;
                inventoryMenu.setVisible(true);
            }
        }
    }

    private void updateCamera(float delta) {
        if (controller.isPaused || controller.isInventoryOpen || controller.isVictoryTriggered) return;

        float targetX = world.getPlayer().getX() + (world.getPlayer().getWidth() / 2f);
        float targetY = world.getPlayer().getY() + (world.getPlayer().getHeight() / 2f);

        float camHalfWidth = 1280 / 2f;
        float camHalfHeight = 720 / 2f;
        float minCamX = camHalfWidth;
        float maxCamX = Math.max(camHalfWidth, world.getMapWidth() - camHalfWidth);
        float minCamY = camHalfHeight;
        float maxCamY = Math.max(camHalfHeight, world.getMapHeight() - camHalfHeight);

        if (world.isBossFightActive() && world.getBossArenaBounds() != null) {
            minCamX = Math.max(camHalfWidth, world.getBossArenaBounds().x + camHalfWidth);
            maxCamX = Math.min(maxCamX, world.getBossArenaBounds().x + world.getBossArenaBounds().width - camHalfWidth);
            if (world.getBossArenaBounds().width < 1280) minCamX = maxCamX = world.getBossArenaBounds().x + (world.getBossArenaBounds().width / 2f);

            minCamY = world.getBossArenaBounds().y + camHalfHeight;
            maxCamY = world.getBossArenaBounds().y + world.getBossArenaBounds().height - camHalfHeight;
            if (world.getBossArenaBounds().height < 720) minCamY = maxCamY = world.getBossArenaBounds().y + (world.getBossArenaBounds().height / 2f);
        }

        camera.position.x = MathUtils.clamp(targetX, minCamX, maxCamX);
        camera.position.y = MathUtils.clamp(targetY, minCamY, maxCamY);

        if (cameraShakeDuration > 0) {
            cameraShakeDuration -= delta;
            float offsetX = MathUtils.random(-cameraShakeIntensity, cameraShakeIntensity);
            float offsetY = MathUtils.random(-cameraShakeIntensity, cameraShakeIntensity);
            camera.position.add(offsetX, offsetY, 0);
            if (cameraShakeDuration <= 0) cameraShakeDuration = 0;
        }
        camera.update();
    }

    private void drawGame(float delta) {
        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{0, 1});

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // enemies
        for (Enemy enemy : world.getEnemies()) {
            if (enemy instanceof HuskHornhead) {
                TextureRegion huskFrame = ((HuskHornhead) enemy).getFrame();
                float offsetX = (huskFrame.getRegionWidth() - enemy.getWidth()) / 2f;
                batch.draw(huskFrame, enemy.getX() - offsetX, enemy.getY());
            } else if (enemy instanceof CrystalGuardian) {
                CrystalGuardian guardian = (CrystalGuardian) enemy;
                TextureRegion guardianFrame = guardian.getFrame();
                float offsetX = (guardianFrame.getRegionWidth() - guardian.getWidth()) / 2f;
                float offsetY = (guardianFrame.getRegionHeight() - guardian.getHeight()) / 2f;
                batch.draw(guardianFrame, guardian.getX() - offsetX, guardian.getY() - offsetY);
                if (guardian.getCurrentState() == CrystalGuardian.GuardianState.LASER && guardian.getLaserBox().width > 0) {
                    TextureRegion laserFrame = GameAssetManager.getEnemyAnimation(EnemyAnimationType.GUARDIAN_LASER_EFFECT).getKeyFrame(guardian.getStateTimer(), true);
                    if (laserFrame.isFlipX() != (guardian.getFacingDirection() == -1)) laserFrame.flip(true, false);
                    Rectangle laserBox = guardian.getLaserBox();
                    batch.draw(laserFrame, laserBox.x, laserBox.y, laserBox.width, laserBox.height);
                }
            } else if (enemy instanceof Mosscreep) {
                TextureRegion mossFrame = ((Mosscreep) enemy).getFrame();
                float offsetX = (mossFrame.getRegionWidth() - enemy.getWidth()) / 2f;
                float offsetY = enemy.isDead() ? (90 - enemy.getHeight()) / 2f : 0;
                batch.draw(mossFrame, enemy.getX() - offsetX, enemy.getY() - offsetY);
            } else if (enemy instanceof Mosquito) {
                TextureRegion mosquitoFrame = ((Mosquito) enemy).getFrame();
                float offsetX = (mosquitoFrame.getRegionWidth() - enemy.getWidth()) / 2f;
                float offsetY = (mosquitoFrame.getRegionHeight() - enemy.getHeight()) / 2f;
                batch.draw(mosquitoFrame, enemy.getX() - offsetX, enemy.getY() - offsetY);
            } else if (enemy instanceof FalseKnight) {
                FalseKnight falseKnight = (FalseKnight) enemy;
                TextureRegion fkFrame = falseKnight.getFrame();
                float offsetX = (fkFrame.getRegionWidth() - falseKnight.getWidth()) / 2f;
                float offsetY = (330 - falseKnight.getHeight()) / 2f;
                batch.draw(fkFrame, falseKnight.getX() - offsetX, falseKnight.getY() - offsetY);
                if (falseKnight.getShockwaveBox().width > 0) {
                    TextureRegion shockwaveFrame = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_SHOCKWAVE).getKeyFrame(falseKnight.getStateTimer(), true);
                    if (shockwaveFrame.isFlipX() != (falseKnight.getShockwaveDirection() == -1)) shockwaveFrame.flip(true, false);
                    Rectangle waveBox = falseKnight.getShockwaveBox();
                    batch.draw(shockwaveFrame, waveBox.x, waveBox.y, waveBox.width, waveBox.height);
                }
            }
        }

        // player
        TextureRegion frame = world.getPlayer().getFrame();
        float offsetX = (349 - world.getPlayer().getWidth()) / 2f;
        float offsetY = (130 - world.getPlayer().getHeight()) / 2f;

        batch.setColor(1, 1, 1, (world.getPlayer().isInvincible() && world.getPlayer().getInvincibilityTimer() % 0.2f > 0.1f) ? 0.5f : 1f);
        batch.draw(frame, world.getPlayer().getX() - offsetX, world.getPlayer().getY() - offsetY);
        batch.setColor(1, 1, 1, 1f);

        if (world.getPlayer().isAttacking()) {
            TextureRegion slashFrame = GameAssetManager.getPlayerAnimation(PlayerAnimationType.SLASH_EFFECT).getKeyFrame(world.getPlayer().getStateTime(), false);
            if (slashFrame.isFlipX() != (world.getPlayer().getFacingDirection() == 1)) slashFrame.flip(true, false);
            float ofsX = slashFrame.getRegionWidth() / 2f;
            float drawX = (world.getPlayer().getFacingDirection() == 1) ? world.getPlayer().getX() + world.getPlayer().getWidth() - ofsX : world.getPlayer().getX() - slashFrame.getRegionWidth() + ofsX;
            batch.draw(slashFrame, drawX, world.getPlayer().getY());

            controller.checkEnemiesGetDamage();
        }

        // particles
        for (Particle particle : world.getAmbientParticles()) {
            batch.setColor(1, 1, 1, particle.getAlpha() * 0.7f);
            batch.draw(particle.getFrame(), particle.getX(), particle.getY());
        }
        // victory item
        if (world.isBossDefeated() && world.getVictoryItemBounds() != null) {
            TextureRegion itemTex = GameAssetManager.getUiElement(UiType.VICTORY_ITEM);
            batch.draw(itemTex, world.getVictoryItemBounds().x, world.getVictoryItemBounds().y, world.getVictoryItemBounds().width, world.getVictoryItemBounds().height);
        }
        batch.setColor(1, 1, 1, 1f);
        batch.end();

        mapRenderer.render(new int[]{2});

//        drawDebugShapes();
    }

    private void drawUI(float delta) {
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        float startX = 50f;
        float startY = 620f;
        float maskSpacing = 60f;

        for (int i = 0; i < world.getPlayer().getMaxHealth(); i++) {
            TextureRegion maskTexture = (i < world.getPlayer().getCurrentHealth()) ? GameAssetManager.getUiElement(UiType.MASK_FULL) : GameAssetManager.getUiElement(UiType.MASK_EMPTY);
            batch.draw(maskTexture, startX + (i * maskSpacing), startY, 35, 50);
        }

        float soulIconX = startX;
        float soulIconY = startY - 60;
        batch.draw(GameAssetManager.getUiElement(UiType.SOUL_ICON), soulIconX, soulIconY, 60, 40);

        BitmapFont uiFont = GameAssetManager.getFont(FontType.HK_SMALL);
        if (uiFont != null) {
            uiFont.draw(batch, world.getPlayer().getCurrentSoul() + " / " + world.getPlayer().getMaxSoul(), soulIconX + 65f, soulIconY + 30f);
        }
        batch.end();

        drawBrightnessLayer();

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    private void drawDebugShapes() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // player
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(world.getPlayer().getX(), world.getPlayer().getY(), world.getPlayer().getWidth(), world.getPlayer().getHeight());

        if (world.getPlayer().isAttacking()) {
            shapeRenderer.setColor(Color.RED);
            Rectangle attack = world.getPlayer().getAttackBox();
            shapeRenderer.rect(attack.x, attack.y, attack.width, attack.height);
        }

        // enemies
        for (Enemy enemy : world.getEnemies()) {

            if (enemy.isInvincible()) {
                shapeRenderer.setColor(Color.RED);
            } else {
                shapeRenderer.setColor(Color.WHITE);
            }
            shapeRenderer.rect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());

            // HuskHornhead
            if (enemy instanceof HuskHornhead) {
                HuskHornhead husk = (HuskHornhead) enemy;
                shapeRenderer.setColor(Color.ORANGE);
                Rectangle vision = husk.getVisionBox();
                shapeRenderer.rect(vision.x, vision.y, vision.width, vision.height);

                if (husk.getCurrentState() == HuskHornhead.HuskState.ATTACK) {
                    shapeRenderer.setColor(Color.RED);
                    Rectangle huskAttack = husk.getEnemyAttackBox();
                    shapeRenderer.rect(huskAttack.x, huskAttack.y, huskAttack.width, huskAttack.height);
                }
            }

            // Crystal Guardian
            else if (enemy instanceof CrystalGuardian) {
                CrystalGuardian guardian = (CrystalGuardian) enemy;
                shapeRenderer.setColor(Color.CYAN);
                Rectangle vision = guardian.getVisionBox();
                shapeRenderer.rect(vision.x, vision.y, vision.width, vision.height);

                if (guardian.getCurrentState() == CrystalGuardian.GuardianState.LASER && guardian.getLaserBox().width > 0) {
                    shapeRenderer.setColor(Color.MAGENTA);
                    Rectangle laser = guardian.getLaserBox();
                    shapeRenderer.rect(laser.x, laser.y, laser.width, laser.height);
                }
            }

            // False Knight
            else if (enemy instanceof FalseKnight) {
                FalseKnight fk = (FalseKnight) enemy;
                if (fk.getMaceHitbox().width > 0) {
                    shapeRenderer.setColor(Color.RED);
                    Rectangle mace = fk.getMaceHitbox();
                    shapeRenderer.rect(mace.x, mace.y, mace.width, mace.height);
                }
                if (fk.getShockwaveBox().width > 0) {
                    shapeRenderer.setColor(Color.YELLOW);
                    Rectangle wave = fk.getShockwaveBox();
                    shapeRenderer.rect(wave.x, wave.y, wave.width, wave.height);
                }
            }
        }

        shapeRenderer.end();
    }

    public void updateCheatsDisplay() {
        StringBuilder active = new StringBuilder("Active Cheats: ");
        if (!controller.isGodModeActive && !controller.isNoclipActive) active.append("None");
        else {
            if (controller.isGodModeActive) active.append("[God Mode] ");
            if (controller.isNoclipActive) active.append("[Noclip] ");
        }
        cheatsDisplay.setText(active.toString());
    }

    public void triggerVictory() {
        controller.isVictoryTriggered = true;
        victoryMenu.setVisible(true);

        int totalSeconds = (int) world.getPlayer().getPlayTime();
        String timeString = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);

        statsLabel.setText("Total Time: " + timeString + "\n\nEnemies Slain: " + world.getPlayer().getEnemiesKilled() + "\n\nTotal Deaths: " + world.getPlayer().getDeaths());

        Color color = victoryMenu.getColor();
        color.a = 0f;
        victoryMenu.setColor(color);
        victoryMenu.addAction(Actions.fadeIn(2.5f));
    }

    public void triggerCameraShake(float duration, float intensity) {
        this.cameraShakeDuration = duration;
        this.cameraShakeIntensity = intensity;
    }
    public void triggerAchievementNotification(Achievement achievement) {
        if (ProfileManager.unlockAchievement(achievement.name())) {
            achievementQueue.add(achievement);

            showNextAchievement();
        }
    }

    private void showNextAchievement() {
        if (isToastShowing || achievementQueue.isEmpty()) {
            return;
        }

        isToastShowing = true;

        Achievement nextAch = achievementQueue.poll();

        GameAssetManager.playNotifSound();
        toastLabel.setText("Achievement Unlocked!\n" + nextAch.getTitle());

        achievementToast.clearActions();
        achievementToast.setPosition(760, -220);

        achievementToast.addAction(Actions.sequence(

            Actions.moveTo(760, 20, 0.5f, Interpolation.exp10Out),
            Actions.delay(3.5f),
            Actions.moveTo(760, -220, 0.5f, Interpolation.exp10In),

            Actions.run(new Runnable() {
                @Override
                public void run() {
                    isToastShowing = false;
                    showNextAchievement();
                }
            })
        ));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.setToOrtho(false, 1280, 720);
        hudCamera.setToOrtho(false, 1280, 720);
    }
}
