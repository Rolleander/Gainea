package com.broll.gainea.client.game;

import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.collections4.map.MultiValueMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MapObjectContainer {

    private GameState game;
    private MultiValueMap<Location, MapObjectRender> objectRenders = new MultiValueMap<>();
    private static float CIRCLE_R = 55f;
    private static float STACK_DISTANCE = 15f;

    public MapObjectContainer(GameState game) {
        super();
        this.game = game;
    }

    public MapObjectRender getObjectRender(NT_BoardObject object) {
        for (Object value : objectRenders.values()) {
            MapObjectRender render = (MapObjectRender) value;
            if (object.equals(render.getObject())) {
                return render;
            }
        }
        return null;
    }

    private void remove(MapObjectRender render) {
        render.remove();
        objectRenders.getCollection(render.getLocation()).remove(render);
    }

    public void update(List<NT_BoardObject> update) {
        List<MapObjectRender> renders = new ArrayList<>();
        this.objectRenders.keySet().forEach(location -> objectRenders.getCollection(location).forEach(render -> render.remove()));
        this.objectRenders.clear();
        update.stream().forEach(o -> {
            Location location = game.getMap().getLocation(o.location);
            if (location != null) {
                MapObjectRender render = MapObjectRender.createRender(game.getContainer(), game.getContainer().ui.skin, o);
                render.selectionListener();
                this.objectRenders.put(location, render);
                renders.add(render);
            }
        });
        objectRenders.keySet().stream().distinct().forEach(this::arrange);
        renders.sort((r1, r2) -> Float.compare(r1.getY(), r2.getY()));
        renders.forEach(render -> {
            game.getContainer().gameStage.addActor(render);
        });
    }

    private void arrange(Location location) {
        Collection<MapObjectRender> objects = objectRenders.getCollection(location);
        MultiValueMap<Integer, MapObjectRender> stacks = new MultiValueMap<>();
        objects.forEach(render -> {
            NT_BoardObject object = render.getObject();
            if (object instanceof NT_Unit) {
                int owner = ((NT_Unit) object).owner;
                stacks.put(owner, render);
                return;
            }
            stacks.put((int) NT_Unit.NO_OWNER, render);
        });
        arrange(location, objects, stacks);
    }

    private void arrange(Location location, Collection<MapObjectRender> objects, MultiValueMap<Integer, MapObjectRender> stacks) {
        int stackCount = stacks.keySet().size();
        float x = location.coordinates.getDisplayX();
        float y = location.coordinates.getDisplayY();
        if (stackCount == 1) {
            //place directly on location center, no other stacks
            arrangeStack(location, x, y, objects);
        } else {
            //arrange stacks around location center
            Iterator<Integer> keys = stacks.keySet().iterator();
            int index = 0;
            while (keys.hasNext()) {
                Collection<MapObjectRender> renders = stacks.getCollection(keys.next());
                float r = (float) (Math.PI * 2) * ((float) index / (float) (stackCount));
                float cx = (float) Math.cos(r) * CIRCLE_R;
                float cy = (float) Math.sin(r) * CIRCLE_R;
                arrangeStack(location, x + cx, y + cy, renders);
                index++;
            }
        }
    }

    private void arrangeStack(Location location, float x, float y, Collection<MapObjectRender> renders) {
        float stackHeight = 0;
        MapObjectRender topOfStack = null;
        for (MapObjectRender render : renders.stream().sorted((a, b) -> Integer.compare(a.getRank(), b.getRank())).collect(Collectors.toList())) {
            render.setPosition(x, y);
            render.setLocation(location);
            render.setStack(renders, stackHeight);
            render.setStackTop(false);
            y += STACK_DISTANCE;
            stackHeight += STACK_DISTANCE;
            topOfStack = render;
        }
        topOfStack.setStackTop(true);
    }

}
