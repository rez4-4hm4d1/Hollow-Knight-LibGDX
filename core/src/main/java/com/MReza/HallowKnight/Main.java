package com.MReza.HallowKnight;

import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.view.screens.MainMenuScreen;
import com.badlogic.gdx.Game;

public class Main extends Game {

    @Override
    public void create() {
        GameAssetManager.init();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
