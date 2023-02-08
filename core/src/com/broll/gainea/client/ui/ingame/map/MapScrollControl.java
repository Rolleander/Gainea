package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.broll.gainea.Gainea;

public class MapScrollControl {

    public static float MAX_ZOOM = 5;
    private boolean animationActive = false;
    private Vector3 start, end;
    private float animationTime;
    private float elpasedTime;
    private float transZ;
    private Vector2 xBounds, yBounds;
    private OrthographicCamera camera;

    public MapScrollControl(Gainea game) {
        this.camera = (OrthographicCamera) game.gameStage.getCamera();
        calcBounds(game);
    }

    private void calcBounds(Gainea game) {
        xBounds = new Vector2(Float.MAX_VALUE, Float.MIN_VALUE);
        yBounds = new Vector2(Float.MAX_VALUE, Float.MIN_VALUE);
        game.state.getMap().getRenders().forEach(render -> {
            xBounds.x = Math.min(xBounds.x, render.getX());
            xBounds.y = Math.max(xBounds.y, render.getX() + render.getWidth());
            yBounds.x = Math.min(yBounds.x, render.getY());
            yBounds.y = Math.max(yBounds.y, render.getY() + render.getHeight());
        });
    }

    public boolean isAnimationActive() {
        return animationActive;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void scrollBy(float x, float y) {
        camera.translate(x, y);
        camera.position.x = MathUtils.clamp(camera.position.x, xBounds.x, xBounds.y);
        camera.position.y = MathUtils.clamp(camera.position.y, yBounds.x, yBounds.y);
    }

    public void scrollTo(float x, float y, float zoom) {
        start = new Vector3(camera.position.x, camera.position.y, camera.zoom);
        end = new Vector3(x, y, zoom);
        elpasedTime = 0;
        float dst = Vector2.dst(start.x, start.y, end.x, end.y);
        animationTime = Math.min(1f, dst / 500f);
        transZ = Math.min(MAX_ZOOM, Math.max(1, (dst / 750f)));
        if (animationTime > 0) {
            animationActive = true;
        }
    }

    public void update(float delta) {
        if (animationTime > 0) {
            //animate
            Interpolation interpolation = Interpolation.circle;
            float progress = Math.min(1f, elpasedTime / animationTime);
            camera.position.x = interpolation.apply(start.x, end.x, progress);
            camera.position.y = interpolation.apply(start.y, end.y, progress);
            if (progress >= 0.5f) {
                camera.zoom = interpolation.apply(transZ, end.z, progress);
            } else {
                camera.zoom = interpolation.apply(start.z, transZ, progress);
            }
            elpasedTime += delta;
            if (progress == 1) {
                animationTime = 0;
                animationActive = false;
            }
        }
    }

}
