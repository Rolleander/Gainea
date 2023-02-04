package com.broll.gainea.server.core.map;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Area extends Location {

    private Set<Location> adjacentLocations = new HashSet<>();
    private AreaType type;
    private String name;
    private AreaID id;

    public Area(AreaID id) {
        this.id = id;
    }

    public AreaID getId() {
        return id;
    }

    public void setType(AreaType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addAdjacentLocation(Location location) {
        adjacentLocations.add(location);
        if (location instanceof Area) {
            ((Area) location).adjacentLocations.add(this);
        }
    }

    public boolean equals(Object o) {
        if (o instanceof Area) {
            return ((Area) o).getNumber() == getNumber();
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    public AreaType getType() {
        return type;
    }

    @Override
    public Set<Location> getConnectedLocations() {
        return adjacentLocations;
    }

}
