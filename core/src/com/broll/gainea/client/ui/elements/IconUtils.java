package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.broll.gainea.Gainea;

public class IconUtils {

    private final static int UNIT_SIZE = 82;

    public static TextureRegion unitIcon(Gainea game, int icon) {
        return new TextureRegion(game.assets.get("textures/units.png", Texture.class), (icon % 10) * UNIT_SIZE, (icon / 10) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
    }
}
