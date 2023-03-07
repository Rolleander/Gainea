package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<BattleObject> getObjects(GameContainer game, NT_Unit[] units) {
        return Arrays.stream(units).map(it -> BotUtils.getObject(game, it)).collect(Collectors.toList());
    }

    public static BattleObject getObject(GameContainer game, NT_Unit unit) {
        if (unit.owner == NT_Unit.NO_OWNER) {
            for (MapObject object : game.getObjects()) {
                if (object.getId() == unit.id && object instanceof BattleObject) {
                    return (BattleObject) object;
                }
            }
        }
        for (Player player : game.getAllPlayers()) {
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

    public static Goal getGoal(GameContainer game, NT_Goal nt) {
        return game.getAllPlayers().stream().flatMap(p -> p.getGoalHandler().getGoals().stream())
                .filter(goal -> goal.getId() == nt.id).findFirst().orElse(null);
    }

    public static List<Location> getLocations(GameContainer game, short[] options) {
        List<Location> locations = new ArrayList<>();
        for (short id : options) {
            locations.add(getLocation(game, id));
        }
        return locations;
    }

    public static <E> E getHighestScoreEntry(List<E> list, Function<E, Integer> scoring) {
        return list.get(getHighestScoreIndex(list, scoring));
    }

    public static <E> int getHighestScoreIndex(List<E> list, Function<E, Integer> scoring) {
        int score = Integer.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            int s = scoring.apply(list.get(i));
            if (s > score) {
                score = s;
                index = i;
            }
        }
        return index;
    }

    public static <E> E getLowestScoreEntry(List<E> list, Function<E, Integer> scoring) {
        return list.get(getLowestScoreIndex(list, scoring));
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

    public static Pair<Location, Integer> getBestPath(Player player, BattleObject object, Collection<Location> fromOptions, Location to) {
        int distance = Integer.MAX_VALUE;
        int units = 0;
        Location location = fromOptions.iterator().next();
        for (Location from : fromOptions) {
            int d = LocationUtils.getWalkingDistance(object, from, to);
            int u = (int) LocationUtils.getUnits(from).stream().filter(it -> it.getOwner() == player).count();
            if (d != -1 && d < distance) {
                distance = d;
                location = from;
                units = u;
            } else if (d == distance && u > units) {
                units = u;
                location = from;
            }
        }
        return Pair.of(location, distance);
    }

    public static Pair<Location, Integer> getBestPath(BattleObject object, Location from, Collection<Location> toOptions) {
        int distance = Integer.MAX_VALUE;
        int units = 0;
        Location location = toOptions.iterator().next();
        for (Location to : toOptions) {
            int d = LocationUtils.getWalkingDistance(object, from, to);
            int u = (int) LocationUtils.getUnits(to).stream().filter(it -> it.getOwner() == object.getOwner()).count();
            if (d != -1 && d < distance) {
                distance = d;
                location = to;
            } else if (d == distance && u > units) {
                units = u;
                location = to;
            }
        }
        return Pair.of(location, distance);
    }

    public static Set<Location> huntOtherPlayersTargets(Player owner, GameContainer game) {
        return game.getAllPlayers().stream().filter(it -> it != owner).flatMap(it -> it.getControlledLocations().stream()).collect(Collectors.toSet());
    }

    public static Set<Location> huntPlayerTargets(Player player) {
        return new HashSet<>(player.getControlledLocations());
    }

}
