package com.broll.gainea.server.core.map;

import com.broll.gainea.server.ExpansionSetting;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapContainer {

    private ExpansionSetting expansionSetting;
    private List<Expansion> expansions;

    public MapContainer(ExpansionSetting setting) {
        this.expansions = MapFactory.create(setting);
    }

    public List<ExpansionType> getActiveExpansionTypes() {
        return expansions.stream().map(Expansion::getType).collect(Collectors.toList());
    }

    public List<Area> getAllAreas() {
        List<Area> areas = new ArrayList<>();
        expansions.forEach(e -> e.getContents().stream().map(AreaCollection::getAreas).forEach(areas::addAll));
        return areas;
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

    public Area getArea(AreaID id) {
        return getAllAreas().stream().filter(it -> it.getId() == id).findFirst().orElse(null);
    }

    public Island getIsland(IslandID id) {
        return getAllIslands().stream().filter(it-> it.getId() ==id).findFirst().orElse(null);
    }

    public Continent getContinent(ContinentID id) {
        return getAllContinents().stream().filter(it-> it.getId() ==id).findFirst().orElse(null);
    }

    public List<Expansion> getExpansions() {
        return expansions;
    }

    public ExpansionSetting getExpansionSetting() {
        return expansionSetting;
    }
}
