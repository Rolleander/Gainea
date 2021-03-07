package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.InGameUI;

public class MapScrollHandler extends InputListener {
    private Gainea game;
    private OrthographicCamera camera;
    private Stage stage;
    private static float DRAG_SPEED = 1;
    private static float SCROLL_SPEED = 30;
    private static float MAX_ZOOM = 5;

    private float lastX, lastY;
    private boolean dragging = false;
    private boolean active = true;
    private Vector3 start, end;
    private float animationTime;
    private float elpasedTime;
    private float transZ;

    public MapScrollHandler(Gainea game, Stage stage) {
        this.game = game;
        this.stage = stage;
        this.camera = (OrthographicCamera) stage.getCamera();
    }

    public void scrollTo(float x, float y, float zoom) {
        start = new Vector3(camera.position.x, camera.position.y, camera.zoom);
        end = new Vector3(x, y, zoom);
        elpasedTime = 0;
        float dst = Vector2.dst(start.x, start.y, end.x, end.y);
        animationTime = Math.min(1f, dst / 500f);
        if (end.z <= start.z) {
            transZ = Math.max(MAX_ZOOM, start.z + (dst / 1000f));
        } else {
            transZ = (end.z + start.z) / 2;
        }
        if (animationTime > 0) {
            active = false;
        }
    }

    public void update(float delta) {
        if (animationTime > 0) {
            //animate
            Interpolation interpolation = Interpolation.circle;
            float progress = Math.min(1f, elpasedTime / animationTime);
            camera.position.x = interpolation.apply(start.x, end.x, progress);
            camera.position.y = interpolation.apply(start.y, end.y, progress);
          /*  if (progress >= 0.5f) {
                camera.zoom = interpolation.apply(transZ, end.z, progress);
            } else {
                camera.zoom = interpolation.apply(start.z, transZ, progress);
            }*/
            camera.zoom = interpolation.apply(start.z, end.z, progress);
            elpasedTime += delta;
            if (progress == 1) {
                animationTime = 0;
                active = true;
            }
        }
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        game.uiStage.setScrollFocus(null);
        Vector2 screenVec = stage.stageToScreenCoordinates(new Vector2(x, y));
        x = screenVec.x;
        y = screenVec.y;
        this.lastX = x;
        this.lastY = y;
        dragging = false;
        return true;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        game.uiStage.setScrollFocus(null);
        InGameUI ui = game.ui.inGameUI;
        if (!dragging && ui != null) {
            ui.clearSelection();
        }
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        game.uiStage.setScrollFocus(null);
        Vector2 screenVec = stage.stageToScreenCoordinates(new Vector2(x, y));
        x = screenVec.x;
        y = screenVec.y;
        float dx = (x - lastX) * DRAG_SPEED * camera.zoom;
        float dy = (y - lastY) * DRAG_SPEED * camera.zoom;
        if (active) {
            camera.translate(-dx, dy);
        }
        this.lastX = x;
        this.lastY = y;
        dragging = true;
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        if (!active) {
            return false;
        }
        camera.zoom *= 1 + ((amount * SCROLL_SPEED) / 100f);
        if (camera.zoom < 1) {
            camera.zoom = 1;
        } else if (camera.zoom > MAX_ZOOM) {
            camera.zoom = MAX_ZOOM;
        }
        return true;
    }

}
