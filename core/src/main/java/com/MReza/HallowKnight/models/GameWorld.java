package com.MReza.HallowKnight.models;

import com.MReza.HallowKnight.models.enemies.Enemy;
import com.MReza.HallowKnight.models.particle.Particle;
import com.MReza.HallowKnight.models.particle.ParticleZone;
import com.MReza.HallowKnight.models.player.Player;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameWorld {
    private Player player;

    private final Array<Enemy> enemies = new Array<>();
    private final Array<Rectangle> collisionRects = new Array<>();
    private final Array<Rectangle> spikeRects = new Array<>();
    private final Array<Rectangle> pitRects = new Array<>();
    private final Array<Rectangle> spawnPoints = new Array<>();
    private final Array<Particle> ambientParticles = new Array<>();
    private final Array<ParticleZone> particleZones = new Array<>();

    private boolean isBossFightActive = false;
    private boolean isBossDefeated = false;
    private Rectangle bossArenaBounds;
    private Rectangle arenaLeftWall;
    private Rectangle arenaRightWall;
    private Rectangle victoryItemBounds;

    private float mapWidth;
    private float mapHeight;
    private float particleSpawnTimer = 0f;

    public GameWorld(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<Rectangle> getCollisionRects() {
        return collisionRects;
    }

    public Array<Rectangle> getSpikeRects() {
        return spikeRects;
    }

    public Array<Rectangle> getPitRects() {
        return pitRects;
    }

    public Array<Rectangle> getSpawnPoints() {
        return spawnPoints;
    }

    public Array<Particle> getAmbientParticles() {
        return ambientParticles;
    }

    public Array<ParticleZone> getParticleZones() {
        return particleZones;
    }

    public boolean isBossFightActive() {
        return isBossFightActive;
    }

    public void setBossFightActive(boolean bossFightActive) {
        isBossFightActive = bossFightActive;
    }

    public boolean isBossDefeated() {
        return isBossDefeated;
    }

    public void setBossDefeated(boolean bossDefeated) {
        isBossDefeated = bossDefeated;
    }

    public Rectangle getBossArenaBounds() {
        return bossArenaBounds;
    }

    public void setBossArenaBounds(Rectangle bossArenaBounds) {
        this.bossArenaBounds = bossArenaBounds;
    }

    public Rectangle getArenaLeftWall() {
        return arenaLeftWall;
    }

    public void setArenaLeftWall(Rectangle arenaLeftWall) {
        this.arenaLeftWall = arenaLeftWall;
    }

    public Rectangle getArenaRightWall() {
        return arenaRightWall;
    }

    public void setArenaRightWall(Rectangle arenaRightWall) {
        this.arenaRightWall = arenaRightWall;
    }

    public Rectangle getVictoryItemBounds() {
        return victoryItemBounds;
    }

    public void setVictoryItemBounds(Rectangle victoryItemBounds) {
        this.victoryItemBounds = victoryItemBounds;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(float mapWidth) {
        this.mapWidth = mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(float mapHeight) {
        this.mapHeight = mapHeight;
    }

    public float getParticleSpawnTimer() {
        return particleSpawnTimer;
    }

    public void setParticleSpawnTimer(float particleSpawnTimer) {
        this.particleSpawnTimer = particleSpawnTimer;
    }
}
