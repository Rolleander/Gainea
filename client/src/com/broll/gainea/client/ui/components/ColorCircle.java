package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.broll.gainea.Gainea;

public class ColorCircle extends Widget {

    private final TextureRegion chip;
    private final static int SIZE = 18;
    private final static int BORDER = 4;

    public ColorCircle(Gainea game, int color) {
        Texture texture = game.assets.get("textures/colors.png", Texture.class);
        this.chip = new TextureRegion(texture, color * SIZE +SIZE, 0, SIZE,SIZE);
    }

    @Override
    public float getPrefHeight() {
        return SIZE + BORDER * 2;
    }

    @Override
    public float getPrefWidth() {
        return SIZE+ BORDER * 2;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(chip, getX()+BORDER, getY()+BORDER);
    }
}
