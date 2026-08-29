package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.net.proto.NT_BoardEffect;
import com.broll.gainea.net.proto.NT_BoardObject;
import java.util.Collection;

public final class GameUtils {
    private GameUtils() {}

    public static void updateMapObjects(Gainea game, Collection<NT_BoardObject> messages) {
        messages.forEach(message -> { removeObject(game, idOf(message)); addMapObject(game, message); });
    }

    public static void removeObjects(Gainea game, Collection<NT_BoardObject> messages) {
        messages.forEach(message -> removeObject(game, idOf(message)));
    }

    private static void removeObject(Gainea game, int id) {
        game.state.getObjects().removeIf(value -> value.id() == id);
        game.state.getPlayers().forEach(player -> player.units().removeIf(value -> value.id() == id));
    }

    public static void updateMapEffects(GameState state, Collection<NT_BoardEffect> effects) {
        effects.forEach(effect -> {
            state.getEffects().removeIf(current -> current.getId() == effect.getId());
            state.getEffects().add(effect);
        });
    }

    public static BoardObjectState addMapObject(Gainea game, NT_BoardObject message) {
        BoardObjectState object = new BoardObjectState(message);
        if (object.isUnit() && object.health() <= 0) return object;
        if (object.isUnit() && object.owner() != -1) {
            PlayerState owner = game.state.getPlayer(object.owner());
            if (owner != null) owner.units().add(object);
        } else game.state.getObjects().add(object);
        return object;
    }

    public static BoardObjectState findObject(Gainea game, int id) {
        for (BoardObjectState object : game.state.getObjects()) if (object.id() == id) return object;
        for (PlayerState player : game.state.getPlayers())
            for (BoardObjectState unit : player.units()) if (unit.id() == id) return unit;
        return null;
    }

    private static int idOf(NT_BoardObject value) {
        if (value.hasMonster()) return value.getMonster().getId();
        if (value.hasUnit()) return value.getUnit().getId();
        return value.getId();
    }
}
