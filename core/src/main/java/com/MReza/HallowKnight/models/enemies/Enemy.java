package com.MReza.HallowKnight.models.enemies;

import com.MReza.HallowKnight.models.GameObject;
import com.MReza.HallowKnight.models.config.AudioConfig;
import com.MReza.HallowKnight.models.player.Player;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public abstract class Enemy extends GameObject {

    protected int hp;
    protected int maxHp;
    protected float velocityX, velocityY;
    protected int facingDirection;
    protected boolean isDead = false;
    protected final float GRAVITY = -1500f;

    protected boolean isInvincible = false;
    protected float invincibilityTimer = 0f;
    protected float INVINCIBILITY_DURATION = 0.5f;

    protected float stateTimer = 0f;



    protected Rectangle groundSensor;

    protected Player player;

    public Enemy(float x, float y, float width, float height, int maxHp, Player player) {
        super(x, y, width, height);
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.facingDirection = -1;
        this.player = player;
        this.groundSensor = new Rectangle();
    }
    @Override
    public abstract void update(float delta, Array<Rectangle> solids);

    protected void applyPhysicsAndCollision(float delta, Array<Rectangle> solids) {
        velocityY += GRAVITY * delta;

        // x dir
        x += velocityX * delta;
        hitbox.setPosition(x, y);

        for (Rectangle rect : solids) {
            if (hitbox.overlaps(rect)) {
                if (velocityX > 0) {
                    x = rect.x - width;
                    onWallHit();
                } else if (velocityX < 0) {
                    x = rect.x + rect.width;
                    onWallHit();
                }
                velocityX = 0;
                hitbox.setPosition(x, y);
            }
        }

        // y dir
        y += velocityY * delta;
        hitbox.setPosition(x, y);

        for (Rectangle rect : solids) {
            if (hitbox.overlaps(rect)) {
                if (velocityY > 0) {
                    y = rect.y - height;
                } else if (velocityY < 0) {
                    y = rect.y + rect.height;
                }
                velocityY = 0;
                hitbox.setPosition(x, y);
            }
        }
    }

    protected abstract void onWallHit();

    public boolean takeDamage(int damage) {
        if (isDead || isInvincible) return false;

        hp -= damage;
        isInvincible = true;
        invincibilityTimer = INVINCIBILITY_DURATION;

        if (hp <= 0) {
            die();
        }
        else {
            playSound(getDamageSound());
        }
        return true;
    }

    protected void updateInvincibility(float delta) {
        if (isInvincible) {
            invincibilityTimer -= delta;
            if (invincibilityTimer <= 0) {
                isInvincible = false;
            }
        }
    }
    public void playSound(List<Sound> sounds) {
        if (AudioConfig.isSfxOn() && sounds != null && !sounds.isEmpty()) {
            int randomIndex = (int) (Math.random() * sounds.size());
            sounds.get(randomIndex).play(1.0f);
        }
    }

    protected abstract List<Sound> getDamageSound();
    protected abstract List<Sound> getDeathSound();

    protected void die() {
        isDead = true;
        velocityX = 0;
        stateTimer = 0;
        playSound(getDeathSound());
    }

    public boolean isDead() { return isDead; }
    public boolean isInvincible() {
        return isInvincible;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public int getFacingDirection() {
        return facingDirection;
    }
}
