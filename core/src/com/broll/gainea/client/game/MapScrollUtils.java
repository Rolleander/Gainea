package com.broll.gainea.client.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.render.MapObjectRender;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.server.core.map.Coordinates;
import com.esotericsoftware.minlog.Log;

public class MapScrollUtils {

    public static void showLocations(Gainea game, int... locations) {
        float minx = 100000;
        float maxx = -100000;
        float miny = 100000;
        float maxy = -100000;
        for (int location : locations) {
            Coordinates coords = game.state.getMap().getLocation(location).getCoordinates();
            float cx = coords.getDisplayX();
            float cy = coords.getDisplayY();
            maxx = Math.max(cx, maxx);
            minx = Math.min(cx, minx);
            maxy = Math.max(cy, maxy);
            miny = Math.min(cy, miny);
        }
        float width = maxx - minx;
        float height = maxy - miny;
        float x = minx + width / 2;
        float y = miny + height / 2;
        float size = Math.max(width, height);
        float zoom = Math.max(size / 750, 1);
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
