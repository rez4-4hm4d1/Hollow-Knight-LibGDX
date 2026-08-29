package com.MReza.HallowKnight.models.player;

public enum CharmType {
    SOUL_CATCHER("Soul Catcher", "Increases Soul gained per strike.","ui/charms/soul.png"),
    DASH_MASTER("DashMaster", "Allows dashing more frequently.","ui/charms/dashMaster.png"),
    UNBREAKABLE_STRENGTH("Unbreakable Strength", "Increases nail damage.","ui/charms/strength.png"),
    QUICK_SLASH("Quick Slash", "Increases attack speed.","ui/charms/slash.png"),
    QUICK_FOCUS("Quick Focus", "Increases healing speed.","ui/charms/focus.png"),
    HEAVY_BLOW("Heavy Blow", "Increases knockback force on enemies.","ui/charms/blow.png"),
    SHARP_SHADOW("Sharp Shadow(empty)", "Dash damages enemies & increases length.","ui/charms/empty.png"),
    VOID_HEART("Void Heart(empty)", "Upgrades spells damage by 50%.","ui/charms/empty.png");

    private final String title;
    private final String description;
    private final String path;

    CharmType(String title, String description, String path) {
        this.title = title;
        this.description = description;
        this.path = path;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public String getPath() {
        return path;
    }
}
