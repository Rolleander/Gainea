package com.broll.gainea.server.core.map;

import com.broll.gainea.net.NT_Location;
import com.broll.gainea.server.core.objects.MapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Location {

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

    public NT_Location nt(){
        NT_Location location =new NT_Location();
        location.x = coordinates.getX(1);
        location.y = coordinates.getY(1);
        return location;
    }
}
