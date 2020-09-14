package com.broll.gainea.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

    private AssetManager manager = new AssetManager();

    public Assets() {
        loadTextures();
        loadUi();
    }

    private void loadTextures() {
        manager.load("ui/loading.png", Texture.class);
        for (int i = 0; i < 4; i++) {
            manager.load("textures/expansion_" + i + ".png", Texture.class);
        }
        manager.load("textures/logo.png", Texture.class);
        manager.load("textures/title.png", Texture.class);
        manager.load("textures/chips.png", Texture.class);
        manager.load("textures/icons.png", Texture.class);
        manager.load("textures/units.png", Texture.class);
        manager.load("textures/dot.png", Texture.class);
        manager.load("textures/unit_plate.png", Texture.class);
        manager.load("textures/map_actions.png", Texture.class);
    }

    private void loadUi() {
        manager.load("ui/cloud-form-ui.json", Skin.class);
    }

    public AssetManager getManager() {
        return manager;
    }

    public <T> T get(String name, Class<T> type) {
        return manager.get(name, type);
    }
}
