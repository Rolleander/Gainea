package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.TextureUtils;

public class IconLabel extends Table {

    public IconLabel(Gainea game, int nr, String text) {
        add(new Image(TextureUtils.icon(game, nr)));
        add(new Label(text, game.ui.skin)).padLeft(10);
    }

    public static IconLabel attack(Gainea game,  int attack) {
        return new IconLabel(game, 0, "" + attack);
    }

    public static IconLabel health(Gainea game,  int health, int maxHealth) {
        String h = "" + health;
        if (maxHealth > 1) {
            h = health + "/" + maxHealth;
        }
        return new IconLabel(game, 1, h);
    }

}
