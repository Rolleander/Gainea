package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.Island;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;

import java.util.List;
import java.util.stream.Collectors;

public class ShipUtils {

    public static Area targetLocation(Ship ship) {
        Location to;
        do {
            to = ship.getTo();
        } while (to instanceof Ship);
        return (Area) to;
    }

    public static Area sourceLocation(Ship ship) {
        Location from;
        do {
            from = ship.getFrom();
        } while (from instanceof Ship);
        return (Area) from;
    }

    public static boolean leadsTo(Ship ship, Area area) {
        Area to = targetLocation(ship);
        return to.getNumber() == area.getNumber();
    }

    public static boolean leadsTo(Ship ship, AreaCollection areaCollection) {
        Area to = targetLocation(ship);
        return to.getContainer() == areaCollection;
    }

    public static boolean leadsTo(Ship ship, Expansion expansion) {
        Area to = targetLocation(ship);
        return to.getContainer().getExpansion() == expansion;
    }

    public static boolean startsFrom(Ship ship, Area area) {
        Area from = sourceLocation(ship);
        return from.getNumber() == area.getNumber();
    }

    public static boolean startsFrom(Ship ship, AreaCollection areaCollection) {
        Area from = sourceLocation(ship);
        return from.getContainer() == areaCollection;
    }

    public static boolean startsFrom(Ship ship, Expansion expansion) {
        Area from = sourceLocation(ship);
        return from.getContainer().getExpansion() == expansion;
    }

    public static boolean connects(Ship ship, Area from, Area to) {
        return startsFrom(ship, from) && leadsTo(ship, to);
    }

    public static boolean connects(Ship ship, AreaCollection from, AreaCollection to) {
        return startsFrom(ship, from) && leadsTo(ship, to);
    }

    public static boolean connects(Ship ship, Expansion from, Expansion to) {
        return startsFrom(ship, from) && leadsTo(ship, to);
    }

    public static List<Ship> getShips(AreaCollection from, AreaCollection to) {
        return from.getShips().stream().filter(it -> ShipUtils.leadsTo(it, to)).collect(Collectors.toList());
    }
}
