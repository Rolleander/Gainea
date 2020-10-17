package com.broll.gainea.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class Assets {

    private AssetManager manager = new AssetManager();

    public Assets() {
        loadTextures();
        loadUi();
        loadSounds();
    }

    private void loadSounds() {
        manager.load("sounds/chime.ogg", Sound.class);
        manager.load("sounds/damage.ogg", Sound.class);
        manager.load("sounds/fanfare.ogg", Sound.class);
        manager.load("sounds/hit.ogg", Sound.class);
        manager.load("sounds/move.ogg", Sound.class);
        manager.load("sounds/recruit.ogg", Sound.class);
        manager.load("sounds/roll.ogg", Sound.class);
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
        manager.load("textures/cards.png", Texture.class);
        manager.load("textures/units.png", Texture.class);
        manager.load("textures/blood.png", Texture.class);
        manager.load("textures/roll_back.png", Texture.class);
        manager.load("textures/star_plates.png", Texture.class);
        manager.load("textures/dot.png", Texture.class);
        manager.load("textures/unit_plate.png", Texture.class);
        manager.load("textures/map_actions.png", Texture.class);
        manager.load("textures/battles.jpg", Texture.class);
    }

    private void loadUi() {
        manager.load("ui/font-export.fnt", BitmapFont.class);
        manager.load("ui/title.fnt", BitmapFont.class);
        manager.load("ui/cloud-form-ui.json", Skin.class);
    }

    public AssetManager getManager() {
        return manager;
    }

    public <T> T get(String name, Class<T> type) {
        return manager.get(name, type);
    }
}
