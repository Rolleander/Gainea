package com.broll.gainea.client.game.map;

import java.util.ArrayList;
import java.util.List;

public final class Expansion {
    private final String name;
    private final String texture;
    private final Coordinates coordinates;
    private final List<AreaCollection> contents = new ArrayList<>();
    private final List<Ship> ships = new ArrayList<>();
    public Expansion(String name, String texture, Coordinates coordinates) { this.name = name; this.texture = texture; this.coordinates = coordinates; }
    public String getName() { return name; }
    public String getTexture() { return texture; }
    public Coordinates getCoordinates() { return coordinates; }
    public List<AreaCollection> getContents() { return contents; }
    public List<Ship> getAllShips() { return ships; }
    public List<Area> getAllAreas() { List<Area> result = new ArrayList<>(); for (AreaCollection value : contents) result.addAll(value.getAreas()); return result; }
}
