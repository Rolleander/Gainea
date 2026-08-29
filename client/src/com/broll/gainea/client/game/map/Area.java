package com.broll.gainea.client.game.map;

public final class Area extends Location {
    private final int type;
    public Area(int number, String name, Coordinates coordinates, int type) { super(number, name, coordinates); this.type = type; }
    public int getType() { return type; }
}
