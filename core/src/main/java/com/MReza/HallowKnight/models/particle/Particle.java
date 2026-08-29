package com.MReza.HallowKnight.models.particle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.MReza.HallowKnight.controllers.GameAssetManager;

public class Particle {
    private float x, y;
    private float vx, vy;
    private float lifeTime;
    private float maxLife;
    private float stateTime;
    private ParticleAnimationType type;

    public Particle(float startX, float startY, ParticleAnimationType type) {
        this.x = startX;
        this.y = startY;
        this.type = type;
        this.stateTime = 0f;
        this.lifeTime = 0f;

        this.vx = MathUtils.random(-30f, 30f);
        this.vy = MathUtils.random(-50f, -20f);

        this.maxLife = MathUtils.random(3f, 6f);
    }

    public void update(float delta) {
        stateTime += delta;
        lifeTime += delta;

        x += vx * delta;
        y += vy * delta;
    }

    public TextureRegion getFrame() {
        return GameAssetManager.getParticleAnimation(type).getKeyFrame(stateTime, true);
    }
    public float getAlpha() {
        float timeRemaining = maxLife - lifeTime;
        float alpha = 1.0f; // normal state

        if (timeRemaining < 1.0f) {
            alpha = timeRemaining / 1.0f;
        }
        else if (lifeTime < 0.5f) {
            alpha = lifeTime / 0.5f;
        }

        return MathUtils.clamp(alpha, 0f, 1f);
    }

    public boolean isDead() {
        return lifeTime >= maxLife;
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
