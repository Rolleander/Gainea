package com.broll.gainea.server.core.utils;

import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Event;
import com.broll.gainea.net.NT_Event_FocusObject;
import com.broll.gainea.net.NT_Event_FocusObjects;
import com.broll.gainea.net.NT_Event_MovedObject;
import com.broll.gainea.net.NT_Event_PlacedObject;
import com.broll.gainea.net.NT_Event_UpdateObjects;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;
import com.google.common.collect.Lists;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UnitControl {
    private final static Logger Log = LoggerFactory.getLogger(UnitControl.class);
    private final static int MOVE_PAUSE = 1500;
    private final static int SPAWN_PAUSE = 1500;
    private final static int DAMAGE_PAUSE = 1000;

    public static void move(GameContainer game, MapObject unit, Location location) {
        move(game, Lists.newArrayList(unit), location);
    }

    public static void move(GameContainer game, List<? extends MapObject> units, Location location) {
        if (units.isEmpty()) return;
        Log.trace("UnitControl: move units [" + units.size() + "] to " + location);
        units.forEach(unit -> GameUtils.place(unit, location));
        NT_Event_MovedObject movedObject = new NT_Event_MovedObject();
        movedObject.objects = units.stream().map(MapObject::nt).toArray(NT_BoardObject[]::new);
        GameUtils.sendUpdate(game, movedObject);
        game.getUpdateReceiver().moved((List<MapObject>) units, location);
        ProcessingUtils.pause(MOVE_PAUSE);
    }

    public static void damage(GameContainer game, Unit unit, int damage) {
        damage(game, unit, damage, null);
    }

    public static void kill(GameContainer game, Unit unit) {
        damage(game, unit, Integer.MAX_VALUE, null);
    }

    public static void heal(GameContainer game, Unit unit, int heal) {
        heal(game, unit, heal, null);
    }

    public static void focus(GameContainer game, MapObject object, int effect) {
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.screenEffect = effect;
        nt.object = object.nt();
        GameUtils.sendUpdate(game, nt);
        ProcessingUtils.pause(DAMAGE_PAUSE);
    }

    public static void focus(GameContainer game, List<? extends MapObject> objects, int effect) {
        objects.stream().map(MapObject::getLocation).distinct().forEach(location -> {
            NT_Event_FocusObjects nt = new NT_Event_FocusObjects();
            nt.screenEffect = effect;
            nt.objects = objects.stream().filter(it -> it.getLocation() == location).map(MapObject::nt).toArray(NT_BoardObject[]::new);
            GameUtils.sendUpdate(game, nt);
            ProcessingUtils.pause(DAMAGE_PAUSE);
        });
    }

    public static void update(GameContainer game, List<? extends MapObject> objects) {
        NT_Event_UpdateObjects nt = new NT_Event_UpdateObjects();
        nt.objects = objects.stream().map(MapObject::nt).toArray(NT_BoardObject[]::new);
        GameUtils.sendUpdate(game, nt);
    }

    public static void heal(GameContainer game, Unit unit, int heal, Consumer<NT_Event_FocusObject> consumer) {
        Log.trace("UnitControl: heal unit " + unit + " for " + heal);
        heal = Math.max(unit.getMaxHealth().getValue() - unit.getHealth().getValue(), heal);
        unit.heal(heal);
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.object = unit.nt();
        nt.screenEffect = NT_Event.EFFECT_HEAL;
        if (consumer != null) {
            consumer.accept(nt);
        }
        GameUtils.sendUpdate(game, nt);
        ProcessingUtils.pause(DAMAGE_PAUSE);
    }

    public static void damage(GameContainer game, Unit unit, int damage, Consumer<NT_Event_FocusObject> consumer) {
        Log.trace("UnitControl: damage unit " + unit + " for " + damage);
        //dont overkill
        damage = Math.min(unit.getHealth().getValue(), damage);
        unit.takeDamage(damage);
        boolean lethal = unit.isDead();
        if (lethal) {
            //remove unit
            boolean removed = GameUtils.remove(game, unit);
            if (!removed) {
                //was already killed/removed, do nothing
                return;
            }
            Log.trace("Damaged unit died and is removed");
        }
        //send update to focus clients on object
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.object = unit.nt();
        nt.screenEffect = NT_Event.EFFECT_DAMAGE;
        if (consumer != null) {
            consumer.accept(nt);
        }
        GameUtils.sendUpdate(game, nt);
        game.getUpdateReceiver().damaged(unit, damage);
        if (lethal) {
            unit.onDeath(null);
            game.getUpdateReceiver().killed(unit, null);
        }
        ProcessingUtils.pause(DAMAGE_PAUSE);
    }


    public static void spawn(GameContainer game, MapObject object, Location location) {
        spawn(game, object, location, null);
    }

    public static void spawn(GameContainer game, MapObject object, Location location, Consumer<NT_Event_PlacedObject> consumer) {
        Log.trace("UnitControl: spawn object " + object + " at " + location);
        if (location == null) {
            Log.error("Cannot spawn object " + object + " on null location");
            return;
        }
        object.init(game);
        Player owner = object.getOwner();
        if (owner == null) {
            game.getObjects().add(object);
        } else {
            owner.getUnits().add((Unit) object);
        }
        if (object instanceof Unit) {
            game.getBuffProcessor().applyGlobalBuffs((Unit) object);
        }
        GameUtils.place(object, location);
        game.getUpdateReceiver().register(object);
        NT_Event_PlacedObject nt = new NT_Event_PlacedObject();
        nt.object = object.nt();
        nt.sound = defaultSpawnSound(object, location);
        if (consumer != null) {
            consumer.accept(nt);
        }
        GameUtils.sendUpdate(game, nt);
        game.getUpdateReceiver().spawned(object, location);
        ProcessingUtils.pause(SPAWN_PAUSE);
    }

    private static String defaultSpawnSound(MapObject object, Location location) {
        String sound = null;
        if (object instanceof Monster) {
            sound = "monster.ogg";
        } else {
            sound = "recruit.ogg";
        }
        return sound;
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
            Log.debug("spawn monster " + monster.getName() + " on " + area);
            spawn(game, monster, area);
        }
    }

    public static void conquer(GameContainer game, List<Unit> units, Location target) {
        Player owner = PlayerUtils.getOwner(units);
        List<Unit> targetUnits;
        if (owner == null) {
            targetUnits = LocationUtils.getUnits(target);
        } else {
            targetUnits = PlayerUtils.getHostileArmy(owner, target);
        }
        if (targetUnits.isEmpty()) {
            move(game, units, target);
        } else {
            game.getBattleHandler().startBattle(units, targetUnits);
        }
    }

    public static void recruit(GameContainer game, Player newOwner, List<Unit> units) {
        recruit(game, newOwner, units, null);
    }

    public static void recruit(GameContainer game, Player newOwner, List<Unit> units, Location newLocation) {
        List<Unit> recruit = units.stream().filter(it -> it.getOwner() != newOwner).collect(Collectors.toList());
        if (recruit.isEmpty()) {
            return;
        }
        List<Unit> moveUnits = recruit.stream().filter(it -> it.isAlive() && newLocation != null &&
                it.getLocation() != newLocation).collect(Collectors.toList());
        List<Unit> updateUnits = ListUtils.subtract(recruit, moveUnits);
        recruit.forEach(it -> {
            Player previousOwner = it.getOwner();
            if (previousOwner == null) {
                game.getObjects().remove(it);
            } else {
                previousOwner.getUnits().remove(it);
            }
            newOwner.getUnits().add(it);
            it.setOwner(newOwner);
            if (it.isDead()) {
                it.heal();
            }
            if (it instanceof Monster) {
                ((Monster) it).removeActionTimer();
            }
        });
        if (!moveUnits.isEmpty()) {
            move(game, moveUnits, newLocation);
        }
        if (!updateUnits.isEmpty()) {
            if (newLocation != null) {
                updateUnits.forEach(it -> it.setLocation(newLocation));
            }
            update(game, updateUnits);
        }
    }

}
