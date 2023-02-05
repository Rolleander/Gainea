package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.InGameUI;

public class MapScrollHandler extends InputListener  {
    private Gainea game;
    private MapScrollControl control;
    private OrthographicCamera camera;

    private static float DRAG_SPEED = 1;
    private static float SCROLL_SPEED = 30;

    private float lastX, lastY;
    private boolean dragging = false;

    public MapScrollHandler(Gainea game) {
        this.game = game;
        this.control = new MapScrollControl(game);
        this.camera = control.getCamera();
    }

    public MapScrollControl getMapScrollControl(){
        return control;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        game.uiStage.setScrollFocus(null);
        Vector2 screenVec = game.gameStage.stageToScreenCoordinates(new Vector2(x, y));
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
        Vector2 screenVec = game.gameStage.stageToScreenCoordinates(new Vector2(x, y));
        x = screenVec.x;
        y = screenVec.y;
        float dx = (x - lastX) * DRAG_SPEED * camera.zoom;
        float dy = (y - lastY) * DRAG_SPEED * camera.zoom;
        if (!control.isAnimationActive()) {
            camera.translate(-dx, dy);
        }
        this.lastX = x;
        this.lastY = y;
        dragging = true;
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        if (control.isAnimationActive()) {
            return false;
        }
        camera.zoom *= 1 + ((amount * SCROLL_SPEED) / 100f);
        if (camera.zoom < 1) {
            camera.zoom = 1;
        } else if (camera.zoom > MapScrollControl.MAX_ZOOM) {
            camera.zoom = MapScrollControl.MAX_ZOOM;
        }
        return true;
    }



}
