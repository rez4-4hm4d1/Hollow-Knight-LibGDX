package com.MReza.HallowKnight.models.environment;

public enum BackGroundType {

    SETTINGS_BACKGROUND("backgrounds/controller_prompt_bg.png"),
    MAIN_MENU_BACKGROUND("backgrounds/controller_prompt_bg 2026.png"),

    ACHIEVEMENTS_BACKGROUND("backgrounds/achievementMenu.png"),
    HELP_BACKGROUND("backgrounds/HelpBg.png"),
    START_GAME_BACKGROUND("backgrounds/StartGameMenuBg.png"),

    FORGOTTEN_CROSSROADS_BG("backgrounds/forgotten crossroads.png"),
    GREEN_PATH_BG("backgrounds/green path.png"),

    FORGOTTEN_CROSSROADS_BANNER("backgrounds/Forgotten_Crossroads_banner.png"),
    GREEN_PATH_BANNER("backgrounds/Green_Path_banner.png"),
    ;

    private final String path;

    BackGroundType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
