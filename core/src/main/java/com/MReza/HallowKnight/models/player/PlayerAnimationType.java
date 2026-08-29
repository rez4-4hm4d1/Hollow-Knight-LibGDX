package com.MReza.HallowKnight.models.player;

import com.MReza.HallowKnight.models.AnimationType;

public enum PlayerAnimationType implements AnimationType {
    IDLE("animations/player_animations/Idle.png", 9, 0.2f),
    RUN("animations/player_animations/Run_new.png", 9, 0.1f),
    DASH("animations/player_animations/Dash.png", 12, 0.05f),
    DASH_EFFECT("animations/player_animations/Dash Effect.png", 8, 0.1f),
    SLASH("animations/player_animations/Slash.png", 5, 0.1f),
    SLASH_EFFECT("animations/player_animations/SlashEffect.png", 6, 0.02f),
    AIRBORNE("animations/player_animations/Airborne.png", 12, 0.1f),
    LANDING("animations/player_animations/Landing.png", 4, 0.1f),
    FALL("animations/player_animations/fall.png", 6, 0.1f),
    DOUBLE_JUMP("animations/player_animations/Double Jump.png", 8, 0.1f),
    RUN_START("animations/player_animations/Start_run.png",4 , 0.05f),
    FOCUS("animations/player_animations/Focus.png", 4, 0.1f),
    FOCUS_START("animations/player_animations/Focus Start.png", 3, 0.1f),
    FOCUS_END("animations/player_animations/Focus End.png", 3, 0.1f),
    DEATH("animations/player_animations/Death.png", 18, 0.1f),

    ;

    private final String path;
    private final int frameCount;
    private final float duration;

    PlayerAnimationType(String path, int frameCount, float duration) {
        this.path = path;
        this.frameCount = frameCount;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public float getDuration() {
        return duration;
    }
}
