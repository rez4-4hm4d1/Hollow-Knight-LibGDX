package com.MReza.HallowKnight.models.environment;

public enum UiType {
    MASK_FULL("ui/elements/HP_full.png"),
    MASK_EMPTY("ui/elements/HP_empty.png"),
    SOUL_ICON("ui/elements/Soul icon.png"),
    VICTORY_ITEM("ui/elements/VictoryItem.png"),
    ACHIEVEMENT_BG("ui/elements/achievement_bg.jpg"),
    ;

    private final String path;

    UiType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
