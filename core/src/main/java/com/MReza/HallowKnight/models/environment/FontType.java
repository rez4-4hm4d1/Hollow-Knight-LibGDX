package com.MReza.HallowKnight.models.environment;

public enum FontType {
    HK_REGULAR("fonts/hk1/HK1.fnt", 0.5f),
    HK_SMALL("fonts/hk1/HK1.fnt", 0.35f),
    HK_MORE_SMALL("fonts/hk1/HK1.fnt", 0.25f)
    ;
    private final String path;
    private final float defaultScale;

    FontType(String path, float defaultScale) {
        this.path = path;
        this.defaultScale = defaultScale;
    }

    public String getPath() {
        return path;
    }

    public float getDefaultScale() {
        return defaultScale;
    }
}
