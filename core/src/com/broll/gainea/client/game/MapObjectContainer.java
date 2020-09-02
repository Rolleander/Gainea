package com.broll.gainea.client.game;

import com.broll.gainea.client.render.MapObjectRender;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.collections4.map.MultiValueMap;

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
    private static float CIRCLE_R = 0.5f;
    private static float STACK_DISTANCE = 0.05f;

    public MapObjectContainer(GameState game) {
        super();
        this.game = game;
    }

    public void update(List<NT_BoardObject> update) {
        this.objectRenders.clear();
        update.stream().forEach(o -> {
            Location location = game.getMap().getLocation(o.location);
            this.objectRenders.put(location, new MapObjectRender(o));
        });
        objectRenders.keySet().stream().distinct().forEach(this::arrange);
    }

    private void arrange(Location location) {
        Collection<MapObjectRender> objects = objectRenders.getCollection(location);
        MultiValueMap<Integer, MapObjectRender> stacks = new MultiValueMap<>();
        AtomicInteger neutralCounter = new AtomicInteger(-1);
        objects.forEach(render -> {
            NT_BoardObject object = render.getObject();
            if (object instanceof NT_Unit) {
                int owner = ((NT_Unit) object).owner;
                if (owner != NT_Unit.NO_OWNER) {
                    //add to player stack
                    stacks.put(owner, render);
                    return;
                }
            }
            //no owner => new stack
            stacks.put(neutralCounter.decrementAndGet(), render);
        });
        arrange(location, objects, stacks);
    }

    private void arrange(Location location, Collection<MapObjectRender> objects, MultiValueMap<Integer, MapObjectRender> stacks) {
        int stackCount = stacks.keySet().size();
        float x = location.getCoordinates().getX(1);
        float y = location.getCoordinates().getY(1);
        if (stackCount == 0) {
            //place directly on location center, no other stacks
            arrangeStack(x, y, objects);
        } else {
            //arrange stacks around location center
            Iterator<Integer> keys = stacks.keySet().iterator();
            int index = 0;
            while (keys.hasNext()) {
                Collection<MapObjectRender> renders = stacks.getCollection(keys.next());
                float r = (float) (Math.PI * 2) * ((float) index / (float) (stackCount - 1));
                float cx = (float) Math.cos(r) * CIRCLE_R;
                float cy = (float) Math.sin(r) * CIRCLE_R;
                arrangeStack(x + cx, y + cy, renders);
                index++;
            }
        }
    }

    private void arrangeStack(float x, float y, Collection<MapObjectRender> renders) {
        for (MapObjectRender render : renders) {
            render.setPosition(x, y);
            y += STACK_DISTANCE;
        }
    }

}
