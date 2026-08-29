package com.MReza.HallowKnight.models.environment;

public enum MapType {
    FORGOTTEN_CROSSROADS("maps/env1/env1.tmx"),
    GREEN_PATH("maps/env2/env2.tmx"),
    ;

    private final String path;

    MapType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
