package com.broll.gainea.server.core.map;

import com.broll.gainea.server.init.ExpansionSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapContainer {

    private ExpansionSetting expansionSetting;
    protected List<Expansion> expansions;
    private Map<Integer, Location> locations = new HashMap<>();

    public MapContainer(ExpansionSetting setting) {
        this.expansionSetting = setting;
        init(setting);
        expansions.stream().flatMap(it -> it.getAllLocations().stream()).forEach(l -> locations.put(l.getNumber(), l));
    }

    protected void init(ExpansionSetting setting) {
        this.expansions = MapFactory.createRenderless(setting);
    }

    public Location getLocation(int number) {
        return locations.get(number);
    }

    public List<ExpansionType> getActiveExpansionTypes() {
        return expansions.stream().map(Expansion::getType).collect(Collectors.toList());
    }

    public List<Area> getAllAreas() {
        List<Area> areas = new ArrayList<>();
        expansions.forEach(e -> e.getContents().stream().map(AreaCollection::getAreas).forEach(areas::addAll));
        return areas;
    }

    public List<Location> getAllLocations() {
        List<Location> areas = new ArrayList<>();
        expansions.forEach(e -> e.getAllLocations().forEach(areas::add));
        return areas;
    }

    public List<Ship> getAllShips() {
        List<Ship> ships = new ArrayList<>();
        expansions.forEach(e -> e.getAllShips().forEach(ships::add));
        return ships;
    }

    public Expansion getExpansion(ExpansionType type) {
        return expansions.stream().filter(it -> it.getType() == type).findFirst().orElse(null);
    }

    public List<Island> getAllIslands() {
        List<Island> islands = new ArrayList<>();
        expansions.forEach(e -> e.getContents().stream().filter(it -> it instanceof Island).map(it -> (Island) it).forEach(islands::add));
        return islands;
    }

    public List<Continent> getAllContinents() {
        List<Continent> continents = new ArrayList<>();
        expansions.forEach(e -> e.getContents().stream().filter(it -> it instanceof Continent).map(it -> (Continent) it).forEach(continents::add));
        return continents;
    }

    public List<AreaCollection> getAllContainers() {
        List<AreaCollection> containers = new ArrayList<>();
        expansions.forEach(e -> e.getContents().forEach(containers::add));
        return containers;
    }

    public Area getArea(AreaID id) {
        return getAllAreas().stream().filter(it -> it.getId() == id).findFirst().orElse(null);
    }

    public Island getIsland(IslandID id) {
        return getAllIslands().stream().filter(it -> it.getId() == id).findFirst().orElse(null);
    }

    public Continent getContinent(ContinentID id) {
        return getAllContinents().stream().filter(it -> it.getId() == id).findFirst().orElse(null);
    }

    public List<Expansion> getExpansions() {
        return expansions;
    }

    public ExpansionSetting getExpansionSetting() {
        return expansionSetting;
    }
}
