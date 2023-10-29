package com.broll.gainea.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.client.misc.TextAsset;
import com.broll.gainea.client.misc.TextAssetLoader;

import java.util.Arrays;


public class Assets {

    private final static String SOUNDS = "sounds/";
    private final static String MUSIC = "music/";
    private AssetManager manager = new AssetManager();

    public Assets() {
        manager.setLoader(TextAsset.class, new TextAssetLoader(new InternalFileHandleResolver()));
        manager.load("version.txt", TextAsset.class);
        loadTextures();
        loadSounds();
        loadMusic();
        loadUi();
    }

    private void loadMusic() {
        Arrays.stream(new String[]{
                "celtic.mp3",
        }).forEach(it -> manager.load(MUSIC + it, Music.class));
    }

    private void loadSounds() {
        Arrays.stream(new String[]{
                "chime.ogg",
                "damage.ogg",
                "fanfare.ogg",
                "hit.ogg",
                "move.ogg",
                "recruit.ogg",
                "roll.ogg",
                "monster.ogg",
                "goddrake.ogg",
                "spawn.ogg",
                "select.ogg",
                "monster_death.ogg",
                "button.ogg",
                "death_female.ogg",
                "death_male.ogg",
                "battle.ogg"
        }).forEach(it -> manager.load(SOUNDS + it, Sound.class));
    }


    private void loadTextures() {
        manager.load("ui/loading.png", Texture.class);
        for (int i = 0; i < 4; i++) {
            manager.load("textures/expansion_" + i + ".png", Texture.class);
        }
        for (int i = 0; i < 7; i++) {
            manager.load("textures/battles/battle_" + i + ".jpg", Texture.class);
        }
        for (int i = 0; i < 18; i++) {
            manager.load("textures/cards/cards_" + i + ".png", Texture.class);
        }
        manager.load("textures/logo.png", Texture.class);
        manager.load("textures/white.png", Texture.class);
        manager.load("textures/title.jpg", Texture.class);
        manager.load("textures/chips.png", Texture.class);
        manager.load("textures/icons.png", Texture.class);
        manager.load("textures/units.png", Texture.class);
        manager.load("textures/ship.png", Texture.class);
        manager.load("textures/blood.png", Texture.class);
        manager.load("textures/roll_back.png", Texture.class);
        manager.load("textures/star_plates.png", Texture.class);
        manager.load("textures/dot.png", Texture.class);
        manager.load("textures/unit_plate.png", Texture.class);
        manager.load("textures/map_actions.png", Texture.class);
        manager.load("textures/bubble.png", Texture.class);
        manager.load("textures/colors.png", Texture.class);
        manager.load("textures/fire.png", Texture.class);
        manager.load("textures/selection_ring.png", Texture.class);
        manager.load("textures/active_ring.png", Texture.class);
        manager.load("textures/gate.png", Texture.class);
        manager.load("textures/water.jpg", Texture.class);
        manager.load("textures/indicator.png", Texture.class);
        manager.load("textures/menu.png", Texture.class);
        manager.load("textures/endturn.png", Texture.class);
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
        if (type == Music.class) {
            return manager.get(MUSIC + name, type);
        } else if (type == Sound.class) {
            return manager.get(SOUNDS + name, type);
        }
        return manager.get(name, type);
    }

}
