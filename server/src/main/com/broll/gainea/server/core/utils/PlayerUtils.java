package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PlayerUtils {

    public static void iteratePlayers(GameContainer game, int pauseBetween, Consumer<Player> consumer) {
        int current = game.getCurrentTurn();
        ArrayList<Player> players = new ArrayList<>(game.getActivePlayers());
        for (int i = 0; i < players.size(); i++) {
            int nr = (current + i) % players.size();
            consumer.accept(players.get(nr));
            ProcessingUtils.pause(pauseBetween);
        }
    }

    public static boolean isCommanderAlive(Player player) {
        return getCommander(player).isPresent();
    }

    public static Stream<Player> getOtherPlayers(GameContainer game, Player player) {
        return game.getAllPlayers().stream().filter(it -> it != player);
    }

    public static Optional<Soldier> getCommander(Player player) {
        return player.getUnits().stream().filter(PlayerUtils::isCommander).map(it -> (Soldier) it).findFirst();
    }

    public static boolean isCommander(Unit unit) {
        return unit instanceof Soldier && ((Soldier) unit).isCommander();
    }

    public static List<Unit> getUnits(Player player, Location location) {
        return player.getUnits().stream().filter(it -> it.getLocation() == location).collect(Collectors.toList());
    }

    public static List<Unit> getHostileArmy(Player player, Location location) {
        return location.getInhabitants().stream().filter(inhabitant -> isHostile(player, inhabitant))
                .map(o -> (Unit) o).collect(Collectors.toList());
    }

    private static boolean isHostile(Player player, MapObject object) {
        if (object instanceof Unit) {
            return player.getFraction().isHostile((Unit) object);
        }
        return false;
    }

    public static boolean hasHostileArmy(Player player, Location location) {
        return location.getInhabitants().stream().anyMatch(inhabitant -> isHostile(player, inhabitant));
    }

    public static Set<Location> getHostileLocations(GameContainer game, Player player) {
        Set<Location> locations = new HashSet<>();
        getOtherPlayers(game, player).map(Player::getControlledLocations).forEach(locations::addAll);
        locations.removeIf(location -> getHostileArmy(player, location).isEmpty());
        return locations;
    }

    public static Player getOwner(List<Unit> units) {
        return units.stream().map(Unit::getOwner).filter(Objects::nonNull).findFirst().orElse(null);
    }


}
