package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.InGameUI;

public class MapScrollGestureHandler extends ActorGestureListener {
    private Gainea game;
    private MapScrollControl control;
    private OrthographicCamera camera;
    private static float DRAG_SPEED = 0.9f;

    public MapScrollGestureHandler(Gainea game) {
        this.game = game;
        this.control = new MapScrollControl(game);
        this.camera = control.getCamera();
    }

    public MapScrollControl getMapScrollControl(){
        return control;
    }

    @Override
    public void tap(InputEvent event, float x, float y, int count, int button) {
        game.uiStage.setScrollFocus(null);
        InGameUI ui = game.ui.inGameUI;
        if (ui != null) {
            ui.clearSelection();
        }
    }

    @Override
    public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
        game.uiStage.setScrollFocus(null);
        float dx = deltaX * DRAG_SPEED ;
        float dy = deltaY * DRAG_SPEED;
        if (!control.isAnimationActive()) {
            camera.translate(-dx, -dy);
        }
    }

    @Override
    public void zoom(InputEvent event, float initialDistance, float distance) {
        if(control.isAnimationActive()){
            return;
        }
        float zoom = 1 + (initialDistance - distance) / 1000f;
        if (zoom < 0) {
            camera.zoom *= (zoom * -1);
        } else {
            camera.zoom *= zoom;
        }
        if (camera.zoom < 1) {
            camera.zoom = 1;
        } else if (camera.zoom > MapScrollControl.MAX_ZOOM) {
            camera.zoom = MapScrollControl.MAX_ZOOM;
        }
    }
}
