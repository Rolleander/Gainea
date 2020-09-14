package com.broll.gainea.server.core.map;

import java.util.HashSet;
import java.util.Set;

public class Area extends Location {

    private Set<Location> adjacentLocations = new HashSet<>();
    private AreaType type;
    private String name;
    private AreaCollection container;
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

    public void setContainer(AreaCollection container) {
        this.container = container;
    }

    public AreaCollection getContainer() {
        return container;
    }

    public void addAdjacentLocation(Location location) {
        adjacentLocations.add(location);
        if (location instanceof Area) {
            ((Area) location).adjacentLocations.add(this);
        }
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
