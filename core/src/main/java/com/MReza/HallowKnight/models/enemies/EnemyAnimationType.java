package com.MReza.HallowKnight.models.enemies;

import com.MReza.HallowKnight.models.AnimationType;

public enum EnemyAnimationType implements AnimationType {
    HUSK_WALK("animations/enemy_animations/husk/Walk.png", 7, 0.15f),
    HUSK_ATTACK("animations/enemy_animations/husk/Attack Lunge.png", 12, 0.1f),
    HUSK_DEATH("animations/enemy_animations/husk/Death Land.png", 8, 0.1f),
    HUSK_IDLE("animations/enemy_animations/husk/Idle.png", 6, 0.1f),

    GUARDIAN_DEATH("animations/enemy_animations/crystalG/Death.png", 3, 0.1f),
    GUARDIAN_SHOOT("animations/enemy_animations/crystalG/Shoot.png", 7, 0.1f),
    GUARDIAN_RUN("animations/enemy_animations/crystalG/Run.png", 6, 0.1f),
    GUARDIAN_IDLE("animations/enemy_animations/crystalG/Idle.png", 5, 0.1f),
    GUARDIAN_LASER_EFFECT("animations/enemy_animations/crystalG/Laser.png", 8, 0.025f),

    MOSSCREEP_WALK("animations/enemy_animations/mosscreep/Walk.png", 3, 0.1f),
    MOSSCREEP_DEATH("animations/enemy_animations/mosscreep/Death.png", 2, 0.35f),


    MOSQUITO_DEATH("animations/enemy_animations/mosquito/Death.png", 3, 0.15f),
    MOSQUITO_ATTACK("animations/enemy_animations/mosquito/Attack.png", 3, 0.1f),
    MOSQUITO_FLY("animations/enemy_animations/mosquito/Idle.png", 8, 0.085f),

    FALSE_KNIGHT_DEATH("animations/enemy_animations/falseKnight/Death.png",11, 0.1f),
    FALSE_KNIGHT_SLEEP("animations/enemy_animations/falseKnight/Idle.png",5, 0.1f),
    FALSE_KNIGHT_IDLE("animations/enemy_animations/falseKnight/Idle.png",5, 0.1f),
    FALSE_KNIGHT_RUN("animations/enemy_animations/falseKnight/Run2.png",4, 0.1f),
    FALSE_KNIGHT_MACE_SLAM("animations/enemy_animations/falseKnight/Attack3.png",5, 0.1f),
    FALSE_KNIGHT_JUMP("animations/enemy_animations/falseKnight/Jump.png",4, 0.1f),
    FALSE_KNIGHT_POWER_SLAM("animations/enemy_animations/falseKnight/Jump Attack.png",8, 0.1f),
    FALSE_KNIGHT_STUNNED("animations/enemy_animations/falseKnight/Body.png",5, 0.1f),
    FALSE_KNIGHT_SHOCKWAVE("animations/enemy_animations/falseKnight/shock.png",8, 0.1f),
    ;

    private final String path;
    private final int frameCount;
    private final float duration;

    EnemyAnimationType(String path, int frameCount, float duration) {
        this.path = path;
        this.frameCount = frameCount;
        this.duration = duration;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getFrameCount() {
        return frameCount;
    }

    @Override
    public float getDuration() {
        return duration;
    }
}
