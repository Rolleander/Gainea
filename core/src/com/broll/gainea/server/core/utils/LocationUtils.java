package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LocationUtils {

    public static short[] getLocationNumbers(Collection<Location> locations){
        short[] numbers = new short[locations.size()];
        Iterator<Location> iterator = locations.iterator();
        int i=0;
        while(iterator.hasNext()){
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
        return player.getControlledLocations().stream().filter(it -> it.getContainer().getExpansion().getType() == ExpansionType.GAINEA).collect(Collectors.toList());
    }

    public static boolean emptyOrControlled(Location location, Player player) {
        return location.getInhabitants().stream().map(it -> {
            if (it instanceof BattleObject) {
                return ((BattleObject) it).getOwner() == player;
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
            if (it instanceof BattleObject) {
                return ((BattleObject) it).getOwner() != null;
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

    public static Location getRandomFree(List<? extends Location> locations) {
        List<Location> free = locations.stream().filter(Location::isFree).collect(Collectors.toList());
        if (free.isEmpty()) {
            return null;
        }
        Collections.shuffle(free);
        return free.get(0);
    }

}
