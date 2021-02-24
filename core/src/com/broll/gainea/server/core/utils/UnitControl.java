package com.broll.gainea.server.core.utils;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Event_FocusObject;
import com.broll.gainea.net.NT_Event_MovedObject;
import com.broll.gainea.net.NT_Event_PlacedObject;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;
import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UnitControl {
    private final static int MOVE_PAUSE = 1000;
    private final static int SPAWN_PAUSE = 1000;
    private final static int DAMAGE_PAUSE = 500;

    public static void move(GameContainer game, MapObject unit, Location location) {
        move(game, Lists.newArrayList(unit), location);
    }

    public static void move(GameContainer game, List<? extends MapObject> units, Location location) {
        units.forEach(unit -> unit.setLocation(location));
        NT_Event_MovedObject movedObject = new NT_Event_MovedObject();
        movedObject.objects = units.stream().map(MapObject::nt).toArray(NT_BoardObject[]::new);
        GameUtils.sendUpdate(game, movedObject);
        game.getUpdateReceiver().moved((List<MapObject>) units, location);
        ProcessingUtils.pause(MOVE_PAUSE);
    }

    public static void damage(GameContainer game, BattleObject unit, int damage) {
        damage(game, unit, damage, null);
    }

    public static void kill(GameContainer game, BattleObject unit) {
        damage(game, unit, Integer.MAX_VALUE, null);
    }

    public static void heal(GameContainer game, BattleObject unit, int heal) {
        heal(game, unit, heal, null);
    }

    public static void focus(GameContainer game, MapObject object, int effect) {
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.screenEffect = effect;
        nt.object = object.nt();
        GameUtils.sendUpdate(game, nt);
        ProcessingUtils.pause(DAMAGE_PAUSE);
    }

    public static void heal(GameContainer game, BattleObject unit, int heal, Consumer<NT_Event_FocusObject> consumer) {
        heal = Math.max(unit.getMaxHealth().getValue() - unit.getHealth().getValue(), heal);
        unit.heal(heal);
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.object = unit.nt();
        nt.screenEffect = NT_Abstract_Event.EFFECT_HEAL;
        if (consumer != null) {
            consumer.accept(nt);
        }
        GameUtils.sendUpdate(game, nt);
        ProcessingUtils.pause(DAMAGE_PAUSE);
    }

    public static void damage(GameContainer game, BattleObject unit, int damage, Consumer<NT_Event_FocusObject> consumer) {
        //dont overkill
        damage = Math.max(unit.getHealth().getValue(), damage);
        unit.takeDamage(damage);
        if (unit.isDead()) {
            //remove unit
            Player owner = unit.getOwner();
            boolean removed = false;
            if (owner == null) {
                removed = game.getObjects().remove(unit);
            } else {
                removed = owner.getUnits().remove(unit);
            }
            if (!removed) {
                //was already killed/removed, do nothing
                return;
            }
        }
        //send update to focus clients on object
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.object = unit.nt();
        nt.screenEffect = NT_Abstract_Event.EFFECT_DAMAGE;
        if (consumer != null) {
            consumer.accept(nt);
        }
        GameUtils.sendUpdate(game, nt);
        game.getUpdateReceiver().damaged(unit, damage);
        if (unit.isDead()) {
            game.getUpdateReceiver().killed(unit, null);
        }
        ProcessingUtils.pause(DAMAGE_PAUSE);
    }


    public static void spawn(GameContainer game, MapObject object, Location location) {
        spawn(game, object, location, null);
    }

    public static void spawn(GameContainer game, MapObject object, Location location, Consumer<NT_Event_PlacedObject> consumer) {
        if (location == null) {
            Log.error("Cannot spawn object " + object + " on null location");
            return;
        }
        object.init(game);
        Player owner = object.getOwner();
        if (owner == null) {
            game.getObjects().add(object);
        } else {
            owner.getUnits().add((BattleObject) object);
        }
        if (object instanceof BattleObject) {
            game.getBuffProcessor().applyGlobalBuffs((BattleObject) object);
        }
        object.setLocation(location);
        location.getInhabitants().add(object);
        NT_Event_PlacedObject placedObject = new NT_Event_PlacedObject();
        placedObject.object = object.nt();
        if (consumer != null) {
            consumer.accept(placedObject);
        }
        GameUtils.sendUpdate(game, placedObject);
        game.getUpdateReceiver().spawned(object, location);
        ProcessingUtils.pause(SPAWN_PAUSE);
    }

    public static void spawnMonsters(GameContainer game, int count) {
        List<Area> areas = game.getMap().getAllAreas();
        Collections.shuffle(areas);
        Iterator<Area> freeAreas = areas.stream().filter(Area::isFree).iterator();
        int spawned = 0;
        while (spawned < count && freeAreas.hasNext()) {
            spawnMonster(game, freeAreas.next());
            spawned++;
        }
    }

    public static void spawnMonster(GameContainer game, Area area) {
        List<Monster> activeMonsters = game.getObjects().stream().filter(it -> it instanceof Monster).map(it -> (Monster) it).collect(Collectors.toList());
        Monster monster = game.getMonsterFactory().spawn(area.getType(), activeMonsters);
        if (monster != null) {
            Log.info("spawn monster " + monster.getName() + " on " + area);
            spawn(game, monster, area);
        }
    }

}
