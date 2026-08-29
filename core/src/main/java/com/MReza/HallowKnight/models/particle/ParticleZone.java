package com.MReza.HallowKnight.models.particle;

import com.badlogic.gdx.math.Rectangle;

public class ParticleZone {
    public Rectangle bounds;
    public ParticleAnimationType type;

    public ParticleZone(Rectangle bounds, ParticleAnimationType type) {
        this.bounds = bounds;
        this.type = type;
    }
}
