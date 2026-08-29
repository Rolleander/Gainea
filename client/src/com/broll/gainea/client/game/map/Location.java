package com.broll.gainea.client.game.map;

import java.util.ArrayList;
import java.util.List;

public class Location {
    private final int number;
    private final String name;
    private final Coordinates coordinates;
    private AreaCollection container;
    private final List<Location> connectedLocations = new ArrayList<>();
    public Location(int number, String name, Coordinates coordinates) { this.number = number; this.name = name; this.coordinates = coordinates; }
    public int getNumber() { return number; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public AreaCollection getContainer() { return container; }
    void setContainer(AreaCollection container) { this.container = container; }
    public List<Location> getConnectedLocations() { return connectedLocations; }
    @Override public String toString() { return name; }
}
