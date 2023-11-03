package com.broll.gainea.client.game;

import static com.broll.gainea.net.NT_BoardObject.NO_LOCATION;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.unit.UnitRender;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Event;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapObjectContainer {

    private static float CIRCLE_R = 55f;
    private static float STACK_DISTANCE = 15f;
    private GameState game;
    private Map<Short, MapObjectRender> objectRenders = new HashMap<>();

    public MapObjectContainer(GameState game) {
        super();
        this.game = game;
    }

    public void removeActions() {
        objectRenders.values().forEach(it -> {
            if (it instanceof UnitRender) {
                ((UnitRender) it).setActionActive(false);
            }
        });
    }

    public void updateActiveState(List<NT_Action_Move> moves, List<NT_Action_Attack> attacks) {
        for (MapObjectRender value : objectRenders.values()) {
            if (value instanceof UnitRender) {
                UnitRender render = (UnitRender) value;
                int id = render.getObject().id;
                render.setActionActive(
                        moves.stream().anyMatch(it -> Arrays.stream(it.units).anyMatch(unit -> unit.id == id))
                                || attacks.stream().anyMatch(it -> Arrays.stream(it.units).anyMatch(unit -> unit.id == id))
                );
            }
        }
    }

    public void remove(NT_BoardObject obj) {
        MapObjectRender render = objectRenders.remove(obj.id);
        if (render != null) {
            render.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor()));
        }
    }

    public MapObjectRender getObjectRender(NT_BoardObject object) {
        return objectRenders.get(object.id);
    }

    private NT_BoardObject findObject(List<NT_BoardObject> objects, int id) {
        for (NT_BoardObject object : objects) {
            if (object.id == id) {
                return object;
            }
        }
        return null;
    }

    private void updateRender(MapObjectRender render, NT_BoardObject nt, int effect) {
        render.init(nt);
        Location location = game.getMap().getLocation(nt.location);
        render.setLocation(location);
        boolean damage = effect == NT_Event.EFFECT_DAMAGE;
        if (render instanceof UnitRender) {
            UnitRender unitRender = (UnitRender) render;
            if (unitRender.getUnit().health <= 0) {
                this.objectRenders.remove(nt.id);
                if (damage) {
                    unitRender.kill(true);
                    //todo update stack after killed
                } else {
                    unitRender.remove();
                }
            } else {
                if (damage) {
                    unitRender.takeDamage(0);
                } else if (effect == NT_Event.EFFECT_BUFF) {
                    //todo show other effect animations
                }
            }
        }
    }

    private MapObjectRender createRender(NT_BoardObject nt) {
        Location location = game.getMap().getLocation(nt.location);
        MapObjectRender render = MapObjectRender.createRender(game.getContainer(), game.getContainer().ui.skin, nt);
        render.selectionListener();
        render.setLocation(location);
        this.objectRenders.put(nt.id, render);
        game.getContainer().gameStage.addActor(render);
        return render;
    }

    public void update(int effect, List<NT_BoardObject> update) {
        List<MapObjectRender> newRenders = new ArrayList<>();
        update.forEach(nt -> {
            MapObjectRender render = getObjectRender(nt);
            if (render == null && nt.location != NO_LOCATION) {
                newRenders.add(createRender(nt));
            }
        });
        for (MapObjectRender existingRender : CollectionUtils.subtract(objectRenders.values(), newRenders)) {
            NT_BoardObject nt = findObject(update, existingRender.getObject().id);
            if (nt != null) {
                updateRender(existingRender, nt, effect);
            }
        }
        rearrangeStacks();
    }

    public void rearrangeStacks() {
        objectRenders.values().stream().map(MapObjectRender::getLocation).distinct().forEach(this::arrange);
        game.getContainer().gameStage.sort();
    }

    private void arrange(Location location) {
        Collection<MapObjectRender> objects = objectRenders.values().stream().filter(it -> it.getLocation() == location).collect(Collectors.toList());
        MultiValueMap<Integer, MapObjectRender> stacks = new MultiValueMap<>();
        objects.forEach(render -> {
            NT_BoardObject object = render.getObject();
            if (object instanceof NT_Unit) {
                NT_Unit unit = ((NT_Unit) object);
                int owner = unit.owner;
                stacks.put(owner, render);
            } else {
                stacks.put((int) NT_Unit.NO_OWNER, render);
            }
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
        float stackHeight = 0;
        MapObjectRender topOfStack = null;
        for (MapObjectRender render : renders.stream().sorted(Comparator.comparingInt(MapObjectRender::getRank)).collect(Collectors.toList())) {
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
