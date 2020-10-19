package com.broll.gainea.client;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.InGameUI;
import com.esotericsoftware.minlog.Log;

public class MapScrollHandler extends InputListener {
    private Gainea game;
    private OrthographicCamera camera;
    private Stage stage;
    private static float DRAG_SPEED = 1;
    private static float SCROLL_SPEED = 30;

    private float lastX, lastY;
    private boolean dragging=false;

    public MapScrollHandler(Gainea game, Stage stage) {
        this.game = game;
        this.stage = stage;
        this.camera = (OrthographicCamera) stage.getCamera();
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
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
        InGameUI ui = game.ui.inGameUI;
        if (!dragging && ui != null) {
            ui.clearSelection();
        }
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        Vector2 screenVec = stage.stageToScreenCoordinates(new Vector2(x, y));
        x = screenVec.x;
        y = screenVec.y;
        float dx = (x - lastX) * DRAG_SPEED * camera.zoom;
        float dy = (y - lastY) * DRAG_SPEED * camera.zoom;
        camera.translate(-dx, dy);
        this.lastX = x;
        this.lastY = y;
        dragging = true;
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        camera.zoom *= 1 + ((amount * SCROLL_SPEED) / 100f);
        if (camera.zoom < 1) {
            camera.zoom = 1;
        } else if (camera.zoom > 10) {
            camera.zoom = 10;
        }
        return true;
    }

}
