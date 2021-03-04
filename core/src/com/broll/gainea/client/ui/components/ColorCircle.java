package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.broll.gainea.Gainea;

public class ColorCircle extends Widget {

    public final static Color[] PLAYER_COLORS = new Color[]{
            Color.valueOf("ea0000"),
            Color.valueOf("001cea"),
            Color.valueOf("00ff06)"),
            Color.valueOf("ead100"),
            Color.valueOf("cd00ec"),
            Color.valueOf("00ecea"),
            Color.valueOf("eb5300"),
            Color.valueOf("f8f8f8"),
            Color.valueOf("525252"),
    };

    private int color;
    private Gainea game;

    public ColorCircle(Gainea game, int color) {
        this.game = game;
        this.color = color;
    }

    @Override
    public float getPrefHeight() {
        return 15;
    }

    @Override
    public float getPrefWidth() {
        return 22;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        float sx = getX();
        float sy = getY();
        game.uiShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.uiShapeRenderer.setColor(Color.BLACK);
        game.uiShapeRenderer.circle(sx +6, sy + 7, 9);
        game.uiShapeRenderer.setColor(PLAYER_COLORS[color]);
        game.uiShapeRenderer.circle(sx + 6, sy + 7, 8);
        game.uiShapeRenderer.end();
        batch.begin();
    }
}
