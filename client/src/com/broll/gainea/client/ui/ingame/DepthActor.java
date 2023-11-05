package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class DepthActor extends Actor {

    public int depth;
    private Color previousBatchColor;


    protected void setAlphaColor(Batch batch, float alpha) {
        previousBatchColor = batch.getColor().cpy();
        batch.setColor(calcRenderColor(alpha));
    }

    protected void resetAlphaColor(Batch batch) {
        batch.setColor(previousBatchColor);
    }

    private Color calcRenderColor(float alpha) {
        Color c = getColor();
        return new Color(c.r, c.g, c.b, c.a * alpha);
    }

}
