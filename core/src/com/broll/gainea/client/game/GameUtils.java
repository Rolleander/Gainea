package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;

public class GameUtils {

    public static void updateMapObjects(Gainea game, NT_BoardObject... objects) {
        for (NT_BoardObject object : objects) {
            game.state.getObjects().remove(object);
            game.state.getPlayers().forEach(player -> {
                if (ArrayUtils.contains(player.units, object)) {
                    player.units = ArrayUtils.removeElement(player.units, object);
                }
            });
            addMapObject(game, object);
        }
    }

    public static void updateMapEffects(GameState state, NT_BoardEffect[] effects) {
        for(NT_BoardEffect effect: effects){
            state.getEffects().remove(effect);
            state.getEffects().add(effect);
        }
    }

    public static void addMapObject(Gainea game, NT_BoardObject object) {
        if (object instanceof NT_Unit) {
            NT_Unit unit = (NT_Unit) object;
            if (unit.health <= 0) {
                return;
            }
            if (unit.owner == NT_Unit.NO_OWNER) {
                game.state.getObjects().add(object);
            } else {
                NT_Player owner = game.state.getPlayer(unit.owner);
                owner.units = ArrayUtils.add(owner.units, unit);
            }
        } else {
            game.state.getObjects().add(object);
        }
    }

    public static NT_BoardObject findObject(Gainea game, int id) {
        Optional<NT_BoardObject> optionalObject = game.state.getObjects().stream().filter(it -> it.id == id).findFirst();
        if (optionalObject.isPresent()) {
            return optionalObject.get();
        }
        for (NT_Player player : game.state.getPlayers()) {
            for (NT_Unit unit : player.units) {
                if (unit.id == id) {
                    return unit;
                }
            }
        }
        return null;
    }

}
