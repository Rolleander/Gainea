package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BotUtils {

    public static Location getLocation(GameContainer game, int id) {
        return game.getMap().getLocation(id);
    }

    public static BattleObject getObject(GameContainer game, NT_Unit unit) {
        if (unit.owner == NT_Unit.NO_OWNER) {
            for (MapObject object : game.getObjects()) {
                if (object.getId() == unit.id && object instanceof BattleObject) {
                    return (BattleObject) object;
                }
            }
        }
        for (Player player : game.getPlayers()) {
            if (player.getServerPlayer().getId() == unit.owner) {
                for (BattleObject object : player.getUnits()) {
                    if (object.getId() == unit.id) {
                        return object;
                    }
                }
            }
        }
        return null;
    }

    public static List<Location> getLocations(GameContainer game, short[] options) {
        List<Location> locations = new ArrayList<>();
        for (short id : options) {
            locations.add(getLocation(game, id));
        }
        return locations;
    }

    public static <E> E getLowestScoreEntry(List<E> list, Function<E, Integer> scoring) {
        int index = getLowestScoreIndex(list, scoring);
        return list.get(index);
    }

    public static <E> int getLowestScoreIndex(List<E> list, Function<E, Integer> scoring) {
        int score = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            int s = scoring.apply(list.get(i));
            if (s < score) {
                score = s;
                index = i;
            }
        }
        return index;
    }

    public static Pair<Location, Integer> getBestPath(Collection<Location> fromOptions, Location to) {
        int distance = Integer.MAX_VALUE;
        Location location = fromOptions.iterator().next();
        for (Location from : fromOptions) {
            int d = LocationUtils.getWalkingDistance(from, to);
            if (d != -1 && d < distance) {
                d = Integer.MAX_VALUE;
                location = from;
            }
        }
        return Pair.of(location, distance);
    }

    public static Pair<Location, Integer> getBestPath(Location from, Collection<Location> toOptions) {
        int distance = Integer.MAX_VALUE;
        Location location = toOptions.iterator().next();
        for (Location to : toOptions) {
            int d = LocationUtils.getWalkingDistance(from, to);
            if (d != -1 && d < distance) {
                d = Integer.MAX_VALUE;
                location = from;
            }
        }
        return Pair.of(location, distance);
    }

    public static Set<Location> huntPlayersTargets(GameContainer game){
        return game.getPlayers().stream().flatMap(it->it.getControlledLocations().stream()).collect(Collectors.toSet());
    }

    public static Set<Location> huntPlayerTargets(Player player){
        return new HashSet<>(player.getControlledLocations());
    }

}
