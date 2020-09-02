package com.broll.gainea.server.core.map;

import com.broll.gainea.server.core.objects.MapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Location {

    private int number;

    private Coordinates coordinates;

    private List<MapObject> inhabitants = new ArrayList<>();

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public List<MapObject> getInhabitants() {
        return inhabitants;
    }

    public abstract Set<Location> getConnectedLocations();

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
