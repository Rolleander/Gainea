package com.broll.gainea.server.core.utils;


import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Continent;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Island;
import com.broll.gainea.server.core.map.IslandID;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LocationUtils {

    public static short[] getLocationNumbers(Collection<Location> locations) {
        short[] numbers = new short[locations.size()];
        Iterator<Location> iterator = locations.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            numbers[i] = (short) iterator.next().getNumber();
            i++;
        }
        return numbers;
    }

    public static boolean isAreaType(Location location, AreaType... type) {
        if (location instanceof Area) {
            Area area = (Area) location;
            return ArrayUtils.contains(type, area.getType());
        }
        return false;
    }

    public static Stream<Area> filterByType(List<Location> locations, AreaType... type) {
        return locations.stream().filter(it -> {
            return isAreaType(it, type);
        }).map(it -> (Area) it);
    }

    public static List<Location> getControlledLocationsIn(Player player, ExpansionType expansionType) {
        return player.getControlledLocations().stream().filter(it -> it.getContainer().getExpansion().getType() == expansionType).collect(Collectors.toList());
    }

    public static boolean emptyOrControlled(Location location, Player player) {
        return location.getInhabitants().stream().map(it -> {
            if (it instanceof Unit) {
                return ((Unit) it).getOwner() == player;
            }
            return true;
        }).reduce(true, Boolean::logicalAnd);
    }

    public static boolean emptyOrWildMonster(Location location) {
        return location.getInhabitants().stream().map(it -> {
            if (it instanceof Monster) {
                return ((Monster) it).getOwner() == null;
            }
            return false;
        }).reduce(true, Boolean::logicalAnd);
    }

    public static boolean noControlledUnits(Location location) {
        return !location.getInhabitants().stream().anyMatch(it -> {
            if (it instanceof Unit) {
                return ((Unit) it).getOwner() != null;
            }
            return false;
        });
    }

    public static List<Location> getWildMonsterLocations(GameContainer game) {
        return game.getObjects().stream().filter(it -> it instanceof Monster).map(MapObject::getLocation).distinct().collect(Collectors.toList());
    }

    public static List<Monster> getMonsters(Location location) {
        return location.getInhabitants().stream().filter(it -> it instanceof Monster).map(it -> (Monster) it).collect(Collectors.toList());
    }

    public static List<Unit> getUnits(Location location) {
        return location.getInhabitants().stream().filter(it -> it instanceof Unit).map(it -> (Unit) it).collect(Collectors.toList());
    }

    public static Location getRandomFree(List<? extends Location> locations) {
        List<Location> free = locations.stream().filter(Location::isFree).collect(Collectors.toList());
        return RandomUtils.pickRandom(free);
    }

    public static boolean isInContinent(Location location, ContinentID id) {
        if (!(location instanceof Ship) && location.getContainer() instanceof Continent) {
            return ((Continent) location.getContainer()).getId() == id;
        }
        return false;
    }

    public static boolean isInIsland(Location location, IslandID id) {
        if (!(location instanceof Ship) && location.getContainer() instanceof Island) {
            return ((Island) location.getContainer()).getId() == id;
        }
        return false;
    }

    public static List<Area> pickRandom(MapContainer map, int amount) {
        List<Area> areas = map.getAllAreas();
        Collections.shuffle(areas);
        return areas.stream().limit(amount).collect(Collectors.toList());
    }

    public static List<Area> pickRandomEmpty(MapContainer map, int amount) {
        List<Area> areas = map.getAllAreas();
        Collections.shuffle(areas);
        return areas.stream().filter(it -> it.getInhabitants().isEmpty()).limit(amount).collect(Collectors.toList());
    }

    private static Function<Location, List<Location>> routes(MapObject object) {
        return location -> location.getConnectedLocations().stream().filter(object::canMoveTo).collect(Collectors.toList());
    }

    public static int getWalkingDistance(MapObject object, Location from, Location to) {
        List<Location> visited = new ArrayList<>();
        List<Location> remaining;
        Function<Location, List<Location>> routes = routes(object);
        int distance = 0;
        if (from == to) {
            return 0;
        }
        remaining = routes.apply(from);
        do {
            distance++;
            for (Location area : remaining) {
                if (area == to) {
                    return distance;
                }
            }
            visited.addAll(remaining);
            remaining = remaining.stream().flatMap(it -> routes.apply(it).stream()).collect(Collectors.toList());
            remaining.removeAll(visited);
        } while (!remaining.isEmpty());
        return -1;
    }

    public static List<Location> getWalkableLocations(MapObject object, Location from, int maxSteps) {
        List<Location> visited = new ArrayList<>();
        List<Location> remaining;
        Function<Location, List<Location>> routes = routes(object);
        int distance = 0;
        remaining = routes.apply(from);
        while (!remaining.isEmpty() && distance < maxSteps) {
            distance++;
            visited.addAll(remaining);
            remaining = remaining.stream().flatMap(it -> routes.apply(it).stream()).collect(Collectors.toList());
            remaining.removeAll(visited);
        }
        visited.addAll(remaining);
        visited.remove(from);
        return visited;
    }

    public static List<Location> getConnectedLocations(Location location, int maxDistance) {
        List<Location> visited = new ArrayList<>();
        List<Location> remaining = Lists.newArrayList(location);
        int distance = 0;
        while (distance < maxDistance) {
            distance++;
            visited.addAll(remaining);
            remaining = remaining.stream().flatMap(it -> it.getConnectedLocations().stream()).collect(Collectors.toList());
            remaining.removeAll(visited);
        }
        visited.addAll(remaining);
        visited.remove(location);
        return visited;
    }

}
