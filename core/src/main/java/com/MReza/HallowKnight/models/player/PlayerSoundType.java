package com.MReza.HallowKnight.models.player;

import com.MReza.HallowKnight.models.SoundType;

public enum PlayerSoundType implements SoundType {
    SLASH(new String[]{
        "audio/player_sounds/sword_1.wav",
        "audio/player_sounds/sword_2.wav",
        "audio/player_sounds/sword_3.wav",
        "audio/player_sounds/sword_4.wav",
    }),
    DASH(new String[]{}),
    TAKE_DAMAGE(new String[]{
        "audio/player_sounds/damage_1.wav"
    }),
    GET_SOUL(new String[]{
        "audio/player_sounds/soul_1.wav",
        "audio/player_sounds/soul_2.wav",
        "audio/player_sounds/soul_3.wav",
        "audio/player_sounds/soul_4.wav",
        "audio/player_sounds/soul_5.wav",
        "audio/player_sounds/soul_6.wav",
        "audio/player_sounds/soul_7.wav",
    }),
    GET_HEAL(new String[]{
        "audio/player_sounds/heal_1.wav"
    }),
    FOCUS(new String[]{
        "audio/player_sounds/focus_1.wav"
    }),
    DEATH(new String[]{
        "audio/player_sounds/death_1.wav"
    })
    ;

    private final String[] paths;

    PlayerSoundType(String[] paths) {
        this.paths = paths;
    }

    public String[] getPaths() {
        return paths;
    }
}
