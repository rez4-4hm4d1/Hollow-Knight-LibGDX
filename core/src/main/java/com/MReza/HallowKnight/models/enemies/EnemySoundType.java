package com.MReza.HallowKnight.models.enemies;

import com.MReza.HallowKnight.models.SoundType;

public enum EnemySoundType implements SoundType {
    HUSK_DEATH(new String[]{
        "audio/enemy_sounds/husk/husk_damage_3.wav",
    }),
    HUSK_DAMAGE(new String[]{
        "audio/enemy_sounds/husk/husk_damage_1.wav",
        "audio/enemy_sounds/husk/husk_damage_2.wav",
        "audio/enemy_sounds/husk/husk_damage_3.wav",
    }),


    GUARDIAN_DAMAGE(new String[]{
        "audio/enemy_sounds/husk/husk_damage_1.wav",
            "audio/enemy_sounds/husk/husk_damage_2.wav",
            "audio/enemy_sounds/husk/husk_damage_3.wav",
    }),
    GUARDIAN_DEATH(new String[]{
        "audio/enemy_sounds/husk/husk_damage_3.wav",
    }),


    MOSSCREEP_DAMAGE(new String[]{
        "audio/enemy_sounds/husk/husk_damage_1.wav",
        "audio/enemy_sounds/husk/husk_damage_2.wav",
        "audio/enemy_sounds/husk/husk_damage_3.wav",
    }),
    MOSSCREEP_DEATH(new String[]{
        "audio/enemy_sounds/husk/husk_damage_3.wav",
    }),

    MOSQUITO_DAMAGE(new String[]{
        "audio/enemy_sounds/husk/husk_damage_1.wav",
        "audio/enemy_sounds/husk/husk_damage_2.wav",
        "audio/enemy_sounds/husk/husk_damage_3.wav",
    }),
    MOSQUITO_DEATH(new String[]{
        "audio/enemy_sounds/husk/husk_damage_3.wav",
    }),

    FALSE_KNIGHT_DAMAGE(new String[]{
        "audio/enemy_sounds/falseKnight/damage_1.wav",
        "audio/enemy_sounds/falseKnight/damage_2.wav",
        "audio/enemy_sounds/falseKnight/damage_3.wav",
        "audio/enemy_sounds/falseKnight/damage_4.wav",
        "audio/enemy_sounds/falseKnight/damage_5.wav",
    }),
    FALSE_KNIGHT_DEATH(new String[]{
        "audio/enemy_sounds/falseKnight/death.wav",
    }),

    ;

    private final String[] paths;

    EnemySoundType(String[] paths) {
        this.paths = paths;
    }

    public String[] getPaths() {
        return paths;
    }
}
