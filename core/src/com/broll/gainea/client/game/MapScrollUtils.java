package com.broll.gainea.client.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.render.MapObjectRender;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.server.core.map.Coordinates;
import com.esotericsoftware.minlog.Log;

public class MapScrollUtils {

    public static void showLocations(Gainea game, int... locations) {
        float minx = Float.MAX_VALUE;
        float maxx = Float.MIN_VALUE;
        float miny = Float.MAX_VALUE;
        float maxy = Float.MIN_VALUE;
        for (int location : locations) {
            Coordinates coords = game.state.getMap().getLocation(location).getCoordinates();
            float cx = coords.getDisplayX();
            float cy = coords.getDisplayY();
            minx = Math.min(minx, cx);
            maxx = Math.max(maxx, cx);
            miny = Math.min(miny, cy);
            maxy = Math.max(maxy, cy);
        }
        float width = maxx - minx;
        float height = maxy - miny;
        float x = minx + width / 2;
        float y = miny + height / 2;
        float size = Math.max(width, height);
        float zoom = size / 750;
        if (zoom < 1) {
            zoom = 1;
        }
        show(game, x, y, zoom);
    }

    public static void showObject(Gainea game, NT_BoardObject object) {
        MapObjectRender render = game.state.getMapObjectsContainer().getObjectRender(object);
        if (render != null) {
            show(game, render.getX(), render.getY());
        } else {
            showLocations(game, object.location);
        }
    }

    private static void show(Gainea game, float x, float y) {
        show(game, x, y, 1);
    }

    private static void show(Gainea game, float x, float y, float zoom) {
        OrthographicCamera camera = (OrthographicCamera) game.gameStage.getCamera();
        camera.zoom = zoom;
        camera.position.x = x;
        camera.position.y = y;
    }
}
