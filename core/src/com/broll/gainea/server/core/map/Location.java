package com.broll.gainea.server.core.map;

import com.broll.gainea.server.core.objects.MapObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Location {

    private int number;
    private AreaCollection container;
    private boolean traversable = true;

    private Coordinates coordinates;

    public boolean isTraversable() {
        return traversable;
    }

    public void setTraversable(boolean traversable) {
        this.traversable = traversable;
    }

    private Set<MapObject> inhabitants = new HashSet<>();

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Set<MapObject> getInhabitants() {
        return inhabitants;
    }

    public abstract Set<Location> getConnectedLocations();

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setContainer(AreaCollection container) {
        this.container = container;
    }

    public AreaCollection getContainer() {
        return container;
    }

    public boolean isFree(){
        return inhabitants.isEmpty() && isTraversable();
    }

    public List<Location> getWalkableNeighbours(){
        return getConnectedLocations().stream().filter(Location::isTraversable).filter(it-> {
            if(it instanceof Ship){
                return ((Ship)it).passable(this);
            }
            return true;
        }).collect(Collectors.toList());
    }
}
