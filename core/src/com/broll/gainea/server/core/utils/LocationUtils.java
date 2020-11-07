package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LocationUtils {

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

    public static List<Location> getWildMonsterLocations(GameContainer game) {
        return game.getMap().getAllLocations().stream().filter(it -> getWildMonster(it) != null).collect(Collectors.toList());
    }

    public static Monster getWildMonster(Location location) {
        return location.getInhabitants().stream().filter(it -> it instanceof Monster && it instanceof GodDragon == false).map(it -> (Monster) it).filter(it -> it.getOwner() == null).findFirst().orElse(null);
    }

}
