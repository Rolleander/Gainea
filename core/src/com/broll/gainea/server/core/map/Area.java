package com.broll.gainea.server.core.map;

import com.broll.gainea.net.NT_Location;

import java.util.HashSet;
import java.util.Set;

public class Area extends Location {

    private Set<Location> adjacentLocations = new HashSet<>();
    private AreaType type;
    private String name;
    private AreaCollection container;

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

    public void addAdjacentLocation(Location location){
        adjacentLocations.add(location);
        if(location instanceof Area){
            Area adjacentArea = ((Area)location);
            if(!adjacentArea.getAdjacentLocations().contains(this)){
                addAdjacentLocation(this);
            }
        }
    }

    public Set<Location> getAdjacentLocations() {
        return adjacentLocations;
    }

    public AreaType getType() {
        return type;
    }

    @Override
    public Set<Location> getConnectedLocations() {
        return adjacentLocations;
    }

    @Override
    public NT_Location nt() {
        NT_Location location = super.nt();
        location.name = name;
        return location;
    }
}
