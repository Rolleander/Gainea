package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.DepthActor;
import com.broll.gainea.server.core.map.Coordinates;

public class ActionTrail extends DepthActor {

    public boolean hover = false;
    private float[] dotX, dotY;
    private TextureRegion dot;
    private float animationAngle;

    public ActionTrail(Gainea game, int nr, Coordinates from, Coordinates to) {
        this.depth = 200;
        Texture texture = game.assets.get("textures/dot.png", Texture.class);
        dot = new TextureRegion(texture, 30 * nr, 0, 30, 30);
        setVisible(false);
        setPosition(to.getDisplayX(), to.getDisplayY());
        int distance = 50;
        int count = (int) ((Vector2.dst(from.getDisplayX(), from.getDisplayY(), getX(), getY()) - 40) / distance);
        this.dotX = new float[count];
        this.dotY = new float[count];
        float angle = (float) (Math.atan2(to.getDisplayY() - from.getDisplayY(), to.getDisplayX() - from.getDisplayX()) + Math.PI);
        float x = getX();
        float y = getY();
        for (int i = 0; i < count; i++) {
            x += MathUtils.cos(angle) * distance;
            y += MathUtils.sin(angle) * distance;
            dotX[i] = x;
            dotY[i] = y;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = 0.65f;
        if (hover) {
            alpha = 1f;
        }
        setAlphaColor(batch, alpha * parentAlpha);
        animationAngle += 0.1;
        for (int i = 0; i < dotX.length; i++) {
            float dy = (float) (Math.sin(animationAngle + i) * 5);
            batch.draw(dot, dotX[i] - 15, dotY[i] - 15 + dy);
        }
        resetAlphaColor(batch);
    }
}
