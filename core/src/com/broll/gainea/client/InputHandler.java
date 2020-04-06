package com.broll.gainea.client;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class InputHandler implements InputProcessor {

    private OrthographicCamera camera;
    private boolean dragging = false;

    private static float SCROLL_SPEED = 20;

    public InputHandler(OrthographicCamera camera){
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        dragging = true;
        this.lastX = screenX;
        this.lastY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        dragging = false;
        return true;
    }

    private int lastX,lastY;
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dragging) return false;
        //Vector3 location = camera.unproject(new Vector3(screenX, screenY, 0));
        float dx = (screenX -lastX) *camera.zoom;
        float dy = (screenY - lastY) *camera.zoom;
        camera.translate(-dx,dy);
        this.lastX = screenX;
        this.lastY = screenY;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        System.out.println(camera.zoom);
        camera.zoom *= 1+((amount*SCROLL_SPEED)/100f);
        return false;
    }
}
