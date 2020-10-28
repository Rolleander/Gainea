package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class PlayerUtils {

    public static void iteratePlayers(GameContainer game, int pauseBetween, Consumer<Player> consumer) {
        int current = game.getCurrentPlayer();
        for (int i = 0; i < game.getPlayers().size(); i++) {
            int nr = (game.getCurrentPlayer() + i) % game.getPlayers().size();
            consumer.accept(game.getPlayers().get(nr));
            ProcessingUtils.pause(pauseBetween);
        }
    }

    public static boolean isCommanderAlive(Player player) {
        return getCommander(player).isPresent();
    }

    public static Optional<Commander> getCommander(Player player) {
        return player.getUnits().stream().filter(unit -> unit instanceof Commander).map(it -> (Commander) it).findFirst();
    }

    public static List<BattleObject> getUnits(Player player, Location location) {
        return player.getUnits().stream().filter(it -> it.getLocation() == location).collect(Collectors.toList());
    }

    public static List<BattleObject> getHostileArmy(Player player, Location location) {
        return location.getInhabitants().stream().filter(inhabitant -> {
            if (inhabitant instanceof BattleObject) {
                return player.getFraction().isHostile((BattleObject) inhabitant);
            }
            return false;
        }).map(o -> (BattleObject) o).collect(Collectors.toList());
    }

    public static List<Location> getHostileLocations(GameContainer game, Player player) {
        return game.getPlayers().stream().filter(it -> it != player).flatMap(it -> it.getControlledLocations().stream().filter(loc -> !getHostileArmy(it, loc).isEmpty())).collect(Collectors.toList());
    }
}
