package com.MReza.HallowKnight.models.player;

public enum Achievement {
    COMPLETION("Completion", "Beat the game and discover the truth.", "icons/achievement/completion.png"),
    SPEEDRUN("Speedrun", "Beat the game in under 2 hours.", "icons/achievement/speedrun.png"),
    TRUE_HUNTER("True Hunter", "Defeat all types of normal enemies.", "icons/achievement/hunter.png"),
    FALSE_KNIGHT("False Knight", "Defeat the False Knight.", "icons/achievement/false_knight.png"),
    CHARM_COLLECTOR("Charm Collector", "Equip your first charm.", "icons/achievement/charm.png");

    private final String title;
    private final String description;
    private final String iconPath;

    Achievement(String title, String description, String iconPath) {
        this.title = title;
        this.description = description;
        this.iconPath = iconPath;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getIconPath() {
        return iconPath;
    }
}
