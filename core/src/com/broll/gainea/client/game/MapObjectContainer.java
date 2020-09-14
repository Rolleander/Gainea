package com.broll.gainea.client.game;

import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.client.render.MapObjectRender;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.collections4.map.MultiValueMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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

    public List<MapObjectRender> update(List<NT_BoardObject> update) {
        List<MapObjectRender> renders = new ArrayList<>();
        this.objectRenders.clear();
        update.stream().forEach(o -> {
            Location location = game.getMap().getLocation(o.location);
            MapObjectRender render = MapObjectRender.createRender(game.getContainer(), o);
            this.objectRenders.put(location, render);
            renders.add(render);
        });
        objectRenders.keySet().stream().distinct().forEach(this::arrange);
        renders.sort((r1, r2) -> Float.compare(r1.getY(), r2.getY()));
        return renders;
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
            stacks.put(NT_Unit.NO_OWNER, render);
        });
        arrange(location, objects, stacks);
    }

    private void arrange(Location location, Collection<MapObjectRender> objects, MultiValueMap<Integer, MapObjectRender> stacks) {
        int stackCount = stacks.keySet().size();
        float x = location.getCoordinates().getDisplayX();
        float y = location.getCoordinates().getDisplayY();
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
        MapObjectRender topOfStack = null;
        for (MapObjectRender render : renders) {
            render.setPosition(x, y);
            render.setLocation(location);
            y += STACK_DISTANCE;
            render.setStack(renders, false);
            topOfStack = render;
        }
        topOfStack.setStack(renders, true);
    }

}
