package com.broll.gainea.server.core.utils;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.net.NT_Event_Bundle;
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class UnitControl {
    private final static int MOVE_PAUSE = 1000;
    private final static int SPAWN_PAUSE = 1000;
    private final static int DAMAGE_PAUSE = 500;

    public static void move(GameContainer game, List<MapObject> units, Location location) {
        units.forEach(unit -> unit.setLocation(location));
        NT_Event_Bundle bundle = new NT_Event_Bundle();
        bundle.events = units.stream().map(it -> {
            NT_Event_MovedObject movedObject = new NT_Event_MovedObject();
            movedObject.object = it.nt();
            return movedObject;
        }).toArray(NT_Abstract_Event[]::new);
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(bundle);
        game.getUpdateReceiver().moved(units, location);
        ProcessingUtils.pause(MOVE_PAUSE);
    }

    public static void damage(GameContainer game, BattleObject unit, int damage) {
        damage(game, unit, damage, 0);
    }

    public static void heal(GameContainer game, BattleObject unit, int heal) {
        heal(game, unit, heal, 0);
    }

    public static void heal(GameContainer game, BattleObject unit, int heal, int screenEffect) {
        heal = Math.max(unit.getMaxHealth() - unit.getHealth(), heal);
        unit.heal(heal);
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.object = unit.nt();
        nt.screen_effect = screenEffect;
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(nt);
        ProcessingUtils.pause(DAMAGE_PAUSE);
    }

    public static void damage(GameContainer game, BattleObject unit, int damage, int screenEffect) {
        //dont overkill
        damage = Math.max(unit.getHealth(), damage);
        unit.takeDamage(damage);
        if (unit.isDead()) {
            //remove unit
            Player owner = unit.getOwner();
            if (owner == null) {
                game.getObjects().remove(unit);
            } else {
                owner.getUnits().remove(unit);
            }
        }
        //send update to focus clients on object
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.object = unit.nt();
        nt.screen_effect = screenEffect;
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(nt);
        game.getUpdateReceiver().damaged(unit, damage);
        ProcessingUtils.pause(DAMAGE_PAUSE);
    }

    public static void spawn(GameContainer game, MapObject object, Location location) {
        object.setLocation(location);
        location.getInhabitants().add(object);
        NT_Event_PlacedObject placedObject = new NT_Event_PlacedObject();
        placedObject.object = object.nt();
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(placedObject);
        game.getUpdateReceiver().spawned(object, location);
        ProcessingUtils.pause(SPAWN_PAUSE);
    }

    public static void spawnMonsters(GameContainer game, int count) {
        List<Area> areas = game.getMap().getAllAreas();
        Collections.shuffle(areas);
        Iterator<Area> freeAreas = areas.stream().filter(it -> it.getInhabitants().isEmpty()).iterator();
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
            game.getObjects().add(monster);
            spawn(game, monster, area);
        }
    }

}
