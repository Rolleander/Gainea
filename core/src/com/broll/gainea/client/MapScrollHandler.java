package com.broll.gainea.client;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.esotericsoftware.minlog.Log;

public class MapScrollHandler extends InputListener {
    private OrthographicCamera camera;
    private boolean dragging = false;

    private static float DRAG_SPEED = 1;
    private static float SCROLL_SPEED = 20;

    private float lastX,lastY;

    public MapScrollHandler(OrthographicCamera camera){
        this.camera = camera;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        dragging = true;
        this.lastX = x;
        this.lastY = y;
        return true;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        dragging = false;
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
     //   if (!dragging) return;
        //Vector3 location = camera.unproject(new Vector3(screenX, screenY, 0));
        float dx = (x -lastX) *DRAG_SPEED ;
        float dy = (y - lastY)*DRAG_SPEED ;
        camera.translate(-dx,-dy);
        this.lastX =(int) x;
        this.lastY =(int) y;
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        camera.zoom *= 1+((amount*SCROLL_SPEED)/100f);
        return true;
    }

}
