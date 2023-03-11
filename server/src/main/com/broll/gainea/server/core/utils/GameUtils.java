package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class GameUtils {
    private GameUtils() {

    }

    public static boolean isGameEnd(GameContainer game) {
        int maxScore = game.getAllPlayers().stream().mapToInt(it -> it.getGoalHandler().getScore()).max().getAsInt();
        int round = game.getRounds();
        int roundLimit = game.getGameSettings().getRoundLimit();
        int scoreLimit = game.getGameSettings().getPointLimit();
        boolean end = false;
        if (scoreLimit > 0) {
            end = maxScore >= scoreLimit;
        }
        if (roundLimit > 0) {
            end = end && round > roundLimit;
        }
        if (noActivePlayersRemaining(game)) {
            end = true;
        }
        return end;
    }

    public static boolean noActivePlayersRemaining(GameContainer game) {
        return game.getActivePlayers().isEmpty() ||
                game.getActivePlayers().stream().allMatch(it -> it.getServerPlayer().isBot());
    }

    public static void sendUpdate(GameContainer game, Player player, Object update, Object updateForOtherPlayers) {
        player.getServerPlayer().sendTCP(update);
        sendUpdateExceptFor(game, updateForOtherPlayers, player);
    }

    public static void sendUpdateExceptFor(GameContainer game, Object update, Player exceptPlayer) {
        game.getActivePlayers().stream().filter(p -> p != exceptPlayer).forEach(p -> p.getServerPlayer().sendTCP(update));
    }

    public static void sendUpdate(GameContainer game, Object update) {
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(update);
    }

    public static List<BattleObject> getUnits(List<MapObject> objects) {
        return objects.stream().filter(it -> it instanceof BattleObject).map(it -> (BattleObject) it).collect(Collectors.toList());
    }

    public static boolean remove(GameContainer game, MapObject object) {
        Player owner = object.getOwner();
        object.getLocation().getInhabitants().remove(object);
        boolean removed;
        if (owner == null) {
            removed = game.getObjects().remove(object);
        } else {
            removed = owner.getUnits().remove(object);
        }
        if (removed) {
            game.getUpdateReceiver().unregister(object);
        }
        return removed;
    }

    public static void place(MapObject object, Location location) {
        Location previous = object.getLocation();
        if (previous != null) {
            previous.getInhabitants().remove(object);
        }
        object.setLocation(location);
        location.getInhabitants().add(object);
    }

    public static int getTotalStartMonsters(GameContainer game) {
        int expansions = game.getMap().getExpansions().size();
        int monstersPerExpansion = game.getGameSettings().getMonsterCount();
        return expansions * monstersPerExpansion;
    }

    public static int getCurrentMonsters(GameContainer game) {
        return (int) game.getObjects().stream().filter(it -> it instanceof Monster).count();
    }
}
