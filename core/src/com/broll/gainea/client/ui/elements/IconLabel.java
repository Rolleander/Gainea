package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;

public class IconLabel extends Table {

    public IconLabel(Gainea game, Skin skin, int nr, String text) {
        add(new Image(TextureUtils.icon(game, nr)));
        add(new Label(text, skin)).padLeft(10);
    }

    public static IconLabel attack(Gainea game, Skin skin, int attack) {
        return new IconLabel(game, skin, 0, "" + attack);
    }

    public static IconLabel health(Gainea game, Skin skin, int health, int maxHealth) {
        String h = "" + health;
        if (maxHealth > 1) {
            h = health + "/" + maxHealth;
        }
        return new IconLabel(game, skin, 1, h);
    }

}
