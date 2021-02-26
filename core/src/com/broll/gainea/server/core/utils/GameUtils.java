package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class GameUtils {
    private GameUtils() {

    }

    public static void sendUpdate(GameContainer game, Player player, Object update, Object updateForOtherPlayers) {
        player.getServerPlayer().sendTCP(update);
        sendUpdateExceptFor(game, updateForOtherPlayers, player);
    }

    public static void sendUpdateExceptFor(GameContainer game, Object update, Player exceptPlayer) {
        game.getPlayers().stream().filter(p -> p != exceptPlayer).forEach(p -> p.getServerPlayer().sendTCP(update));
    }

    public static void sendUpdate(GameContainer game, Object update) {
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(update);
    }

    public static List<BattleObject> getUnits(List<MapObject> objects){
        return objects.stream().filter(it -> it instanceof BattleObject).map(it -> (BattleObject) it).collect(Collectors.toList());
    }

    public static boolean remove(GameContainer game, MapObject object){
        Player owner = object.getOwner();
        object.getLocation().getInhabitants().remove(object);
        if (owner == null) {
           return  game.getObjects().remove(object);
        } else {
           return owner.getUnits().remove(object);
        }
    }
}
