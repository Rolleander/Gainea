package com.broll.gainea.server.core.map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Expansion {

    private Coordinates coordinates;
    private List<AreaCollection> contents = new ArrayList<>();
    private ExpansionType type;

    public List<AreaCollection> getContents() {
        return contents;
    }

    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        contents.stream().map(AreaCollection::getAreas).forEach(locations::addAll);
        contents.stream().map(AreaCollection::getShips).forEach(locations::addAll);
        return locations;
    }

    public List<Area> getAllAreas() {
        List<Area> locations = new ArrayList<>();
        contents.stream().map(AreaCollection::getAreas).forEach(locations::addAll);
        return locations;
    }

    public void setType(ExpansionType type) {
        this.type = type;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setContents(List<AreaCollection> contents) {
        this.contents = contents;
    }

    public ExpansionType getType() {
        return type;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public List<Island> getIslands() {
        return contents.stream().filter(it -> it instanceof Island).map(it -> (Island) it).collect(Collectors.toList());
    }

    public List<Continent> getContinents() {
        return contents.stream().filter(it -> it instanceof Continent).map(it -> (Continent) it).collect(Collectors.toList());
    }
}
