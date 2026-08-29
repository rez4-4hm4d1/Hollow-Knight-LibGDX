package com.MReza.HallowKnight.models.particle;

import com.MReza.HallowKnight.models.AnimationType;

public enum ParticleAnimationType implements AnimationType {
    LEAF_FALL("animations/particles/leaf.png", 3, 0.1f),
    BUTTERFLY("animations/particles/butterfly3.png", 4, 0.08f);

    private final String path;
    private final int frameCount;
    private final float duration;

    ParticleAnimationType(String path, int frameCount, float duration) {
        this.path = path;
        this.frameCount = frameCount;
        this.duration = duration;
    }

    public String getPath() { return path; }
    public int getFrameCount() { return frameCount; }
    public float getDuration() { return duration; }
}
