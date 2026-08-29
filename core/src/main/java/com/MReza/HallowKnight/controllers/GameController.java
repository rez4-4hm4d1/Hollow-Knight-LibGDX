package com.MReza.HallowKnight.controllers;

import com.MReza.HallowKnight.models.GameWorld;
import com.MReza.HallowKnight.models.enemies.CrystalGuardian;
import com.MReza.HallowKnight.models.enemies.Enemy;
import com.MReza.HallowKnight.models.enemies.FalseKnight;
import com.MReza.HallowKnight.models.enemies.HuskHornhead;
import com.MReza.HallowKnight.models.environment.MusicType;
import com.MReza.HallowKnight.models.particle.Particle;
import com.MReza.HallowKnight.models.particle.ParticleZone;
import com.MReza.HallowKnight.models.player.Achievement;
import com.MReza.HallowKnight.models.player.CharmType;
import com.MReza.HallowKnight.view.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameController {

    private GameWorld world;
    private GameScreen screen;

    public boolean isPaused = false;
    public boolean isInventoryOpen = false;
    public boolean isVictoryTriggered = false;
    public boolean isGodModeActive = false;
    public boolean isNoclipActive = false;

    private static final int MAX_PARTICLES = 20;

    public GameController(GameWorld world, GameScreen screen) {
        this.world = world;
        this.screen = screen;
    }

    public void update(float delta, OrthographicCamera camera) {
        if (isPaused || isInventoryOpen || isVictoryTriggered) {
            return;
        }

        world.getPlayer().addPlayTime(delta);
        handleCheatCodes();

        // check touch winning item
        if (world.isBossDefeated() && world.getVictoryItemBounds() != null) {
            if (world.getPlayer().getHitbox().overlaps(world.getVictoryItemBounds())) {
                screen.triggerVictory();
                world.setVictoryItemBounds(null);
                screen.triggerAchievementNotification(Achievement.COMPLETION);

                // speed run = 10 minutes
                if (world.getPlayer().getPlayTime() < 600) {
                    screen.triggerAchievementNotification(Achievement.SPEEDRUN);
                }
            }
        }

        checkBossFightTrigger();
        world.getPlayer().update(delta, world.getCollisionRects());
        checkBossFightLogic();

        for (Enemy enemy : world.getEnemies()) {
            enemy.update(delta, world.getCollisionRects());
        }

        checkCheckpoints();
        checkEnemyCollisions();
        checkHazards();
        updateParticles(delta, camera);
    }

    private void checkBossFightTrigger() {
        if (!world.isBossFightActive() && world.getBossArenaBounds() != null && !world.getPlayer().isDead()) {
            if (world.getPlayer().getX() > world.getBossArenaBounds().x + 100 && world.getPlayer().getX() < world.getBossArenaBounds().x + world.getBossArenaBounds().width - 100) {
                world.setBossFightActive(true);
                world.getCollisionRects().add(world.getArenaLeftWall());
                world.getCollisionRects().add(world.getArenaRightWall());
            }
        }
    }

    private void checkBossFightLogic() {
        if (world.isBossFightActive() && world.getBossArenaBounds() != null) {
            boolean bossAlive = false;
            FalseKnight theBoss = null;

            for (Enemy enemy : world.getEnemies()) {
                if (enemy instanceof FalseKnight) {
                    theBoss = (FalseKnight) enemy;
                    if (!enemy.isDead()) bossAlive = true;
                }
            }

            if (!bossAlive && theBoss != null && !world.isBossDefeated()) {
                world.setBossDefeated(true);
                world.setBossFightActive(false);
                int itemX = ( Math.abs(theBoss.getX() - world.getArenaLeftWall().x) < Math.abs(theBoss.getX() - world.getArenaRightWall().x) ) ? 1000 : -1000;
                world.setVictoryItemBounds(new Rectangle(theBoss.getX() + itemX, theBoss.getY() + 30, 60, 60));
                GameAssetManager.playMusic(GameAssetManager.getMenuMusic(MusicType.VICTORY));
            }
            else if (!world.getPlayer().isDead() && (world.getPlayer().getX() < world.getBossArenaBounds().x || world.getPlayer().getX() > world.getBossArenaBounds().x + world.getBossArenaBounds().width)) {
                world.setBossFightActive(false);
                if (world.getCollisionRects().contains(world.getArenaLeftWall(), true)) {
                    world.getCollisionRects().removeValue(world.getArenaLeftWall(), true);
                    world.getCollisionRects().removeValue(world.getArenaRightWall(), true);
                }
                if (theBoss != null) theBoss.resetBoss();
            }
        }
    }

    private void checkCheckpoints() {
        for (Rectangle spawn : world.getSpawnPoints()) {
            if (world.getPlayer().getHitbox().overlaps(spawn)) {
                world.getPlayer().setRespawnPoint(spawn.x, spawn.y);
            }
        }
    }
    public void checkEnemiesGetDamage(){
        int damageAmount = ProfileManager.isCharmEquipped(CharmType.UNBREAKABLE_STRENGTH.name()) ? 2 : 1;
        for (Enemy enemy : world.getEnemies()) {
            if (!enemy.isDead()) {
                boolean hitSuccessful = false;
                if (enemy instanceof FalseKnight) {
                    FalseKnight fk = (FalseKnight) enemy;
                    if (fk.getCurrentState() == FalseKnight.BossState.STUNNED && world.getPlayer().getAttackBox().overlaps(fk.getHeadHitbox())) hitSuccessful = fk.takeDamage(damageAmount);
                    else if (fk.getCurrentState() != FalseKnight.BossState.STUNNED && world.getPlayer().getAttackBox().overlaps(fk.getHitbox())) hitSuccessful = fk.takeDamage(damageAmount);
                } else if (world.getPlayer().getAttackBox().overlaps(enemy.getHitbox())) {
                    hitSuccessful = enemy.takeDamage(damageAmount);
                }
                if (hitSuccessful) {
                    if (ProfileManager.isCharmEquipped(CharmType.HEAVY_BLOW.name())) {
                        float knockbackForce = 50f;
                        // blow the enemy
                        enemy.setX(enemy.getX() + (world.getPlayer().getFacingDirection() * knockbackForce));
                    }
                    if (enemy.isDead()){
                        world.getPlayer().incrementKills();
                        if (!(enemy instanceof FalseKnight)) {
                            String enemyType = enemy.getClass().getSimpleName();

                            ProfileManager.ProfileData data = ProfileManager.getData();
                            if (!data.killedEnemyTypes.contains(enemyType)) {
                                data.killedEnemyTypes.add(enemyType);
                                ProfileManager.saveProfile();

                                // (Husk, Guardian, Mosscreep, Mosquito)
                                if (data.killedEnemyTypes.size() >= 4) {
                                    screen.triggerAchievementNotification(Achievement.TRUE_HUNTER);
                                }
                            }
                        }
                    }
                    world.getPlayer().addSoul();
                }
            }
        }
    }
    private void checkEnemyCollisions() {
        for (Enemy enemy : world.getEnemies()) {
            if (!enemy.isDead()) {
                if (world.getPlayer().getHitbox().overlaps(enemy.getHitbox())) {
                    world.getPlayer().takeDamage(1);
                }

                if (enemy instanceof HuskHornhead) {
                    HuskHornhead husk = (HuskHornhead) enemy;
                    if (husk.getCurrentState() == HuskHornhead.HuskState.ATTACK && world.getPlayer().getHitbox().overlaps(husk.getEnemyAttackBox())) {
                        world.getPlayer().takeDamage(1);
                    }
                }
                if (enemy instanceof CrystalGuardian) {
                    CrystalGuardian guardian = (CrystalGuardian) enemy;
                    if (guardian.getCurrentState() == CrystalGuardian.GuardianState.LASER && guardian.getLaserBox().width > 0 && world.getPlayer().getHitbox().overlaps(guardian.getLaserBox())) {
                        world.getPlayer().takeDamage(1);
                    }
                }
                if (enemy instanceof FalseKnight) {
                    FalseKnight fk = (FalseKnight) enemy;
                    if (fk.getMaceHitbox().width > 0 && world.getPlayer().getHitbox().overlaps(fk.getMaceHitbox())) {
                        world.getPlayer().takeDamage(1);
                        if (fk.getCurrentState() == FalseKnight.BossState.POWER_SLAM) {
                            world.getPlayer().takeDamage(1);
                        }
                    }
                    if (fk.getShockwaveBox().width > 0 && world.getPlayer().getHitbox().overlaps(fk.getShockwaveBox())) {
                        world.getPlayer().takeDamage(2);
                    }
                }
            }
        }
    }

    private void checkHazards() {
        for (Rectangle spike : world.getSpikeRects()) {
            if (world.getPlayer().getHitbox().overlaps(spike)) world.getPlayer().takeDamage(1);
        }
        for (Rectangle pit : world.getPitRects()) {
            if (world.getPlayer().getHitbox().overlaps(pit) && !world.getPlayer().isFallingInPit() && !world.getPlayer().isDead()) {
                world.getPlayer().fallInPit();
            }
        }
    }

    private void updateParticles(float delta, OrthographicCamera camera) {
        world.setParticleSpawnTimer(world.getParticleSpawnTimer() + delta);
        if (world.getParticleSpawnTimer() > 2f && world.getAmbientParticles().size < MAX_PARTICLES) {
            world.setParticleSpawnTimer( -MathUtils.random(0f, 1f));
            Array<ParticleZone> activeZones = new Array<>();
            Rectangle cameraBounds = new Rectangle(camera.position.x - 640 - 100, camera.position.y - 360 - 100, 1280 + 200, 720 + 200);

            for (ParticleZone zone : world.getParticleZones()) {
                if (cameraBounds.overlaps(zone.bounds)) activeZones.add(zone);
            }

            if (activeZones.size > 0) {
                ParticleZone targetZone = activeZones.random();
                float spawnX = targetZone.bounds.x + MathUtils.random(0, targetZone.bounds.width);
                float spawnY = targetZone.bounds.y + MathUtils.random(0, targetZone.bounds.height);
                world.getAmbientParticles().add(new Particle(spawnX, spawnY, targetZone.type));
            }
        }

        for (int i = world.getAmbientParticles().size - 1; i >= 0; i--) {
            Particle p = world.getAmbientParticles().get(i);
            p.update(delta);
            if (p.isDead()) world.getAmbientParticles().removeIndex(i);
        }
    }

    private void handleCheatCodes() {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.B) && world.getBossArenaBounds() != null) {
                world.getPlayer().setPosition(world.getBossArenaBounds().x + 100, world.getBossArenaBounds().y + 100);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                isNoclipActive = !isNoclipActive;
                world.getPlayer().setNoclip(isNoclipActive);
                screen.updateCheatsDisplay();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) world.getPlayer().emergencyHeal();
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) world.getPlayer().refillSoul();
            if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
                isGodModeActive = !isGodModeActive;
                world.getPlayer().setGodMode(isGodModeActive);
                screen.updateCheatsDisplay();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                for (Enemy enemy : world.getEnemies()) enemy.setDead(true);
            }
        }
    }
}
