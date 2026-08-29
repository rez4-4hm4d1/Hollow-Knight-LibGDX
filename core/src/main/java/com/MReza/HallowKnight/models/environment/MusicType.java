package com.MReza.HallowKnight.models.environment;

public enum MusicType {
    MENU_MUSIC("audio/side_sounds/Hollow Shade Music.wav"),
    FORGOTTEN_CROSSROADS_MUSIC("audio/side_sounds/Crossroads.mp3"),
    GREEN_PATH_MUSIC("audio/side_sounds/Greenpath.mp3"),
    VICTORY("audio/side_sounds/Victory.wav"),

    ;

    private final String path;

    MusicType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
