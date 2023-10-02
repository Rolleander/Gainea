package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.broll.gainea.Gainea;

public class WaterRender extends Actor {

    private final static int S = 256;
    private final static int XC = 40;
    private final static int YC = 40;
    private Texture water;
    private OrthographicCamera camera;
    private float movement;
    private float displacement;

    public WaterRender(Gainea game) {
        water = game.assets.get("textures/water.jpg", Texture.class);
        camera = (OrthographicCamera) game.gameStage.getCamera();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        movement += Gdx.graphics.getDeltaTime() * 12;
        displacement += Gdx.graphics.getDeltaTime() / 3;
        float cw = (camera.viewportWidth + S * 4) * camera.zoom;
        float ch = (camera.viewportHeight + S * 4) * camera.zoom;
        float lx = camera.position.x - cw / 2;
        float ly = camera.position.y - ch / 2;
        float xd = lx % S;
        float yd = ly % S;
        for (float x = lx; x <= lx + cw; x += S) {
            for (float y = ly; y <= ly + ch; y += S) {
                float m = movement % S;
                float jump = (float) (Math.cos(displacement) * S / 6);
                batch.draw(water, x - xd - m, y - yd + m - jump);
            }
        }
    }
}
