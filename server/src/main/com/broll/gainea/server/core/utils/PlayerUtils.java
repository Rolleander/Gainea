package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PlayerUtils {

    public static void iteratePlayers(GameContainer game, int pauseBetween, Consumer<Player> consumer) {
        int current = game.getCurrentPlayer();
        for (int i = 0; i < game.getPlayers().size(); i++) {
            int nr = (current + i) % game.getPlayers().size();
            consumer.accept(game.getPlayers().get(nr));
            ProcessingUtils.pause(pauseBetween);
        }
    }

    public static boolean isCommanderAlive(Player player) {
        return getCommander(player).isPresent();
    }

    public static Stream<Player> getOtherPlayers(GameContainer game, Player player) {
        return game.getPlayers().stream().filter(it -> it != player);
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


    public static Set<Location> getHostileLocations(GameContainer game, Player player) {
        Set<Location> locations = new HashSet<>();
        getOtherPlayers(game, player).map(Player::getControlledLocations).forEach(locations::addAll);
        locations.removeIf(location -> getHostileArmy(player, location).isEmpty());
        return locations;
    }

    public static Player getOwner(List<BattleObject> units) {
        return units.stream().map(it -> it.getOwner()).filter(it -> it != null).findFirst().orElse(null);
    }
}
