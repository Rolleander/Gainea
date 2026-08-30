package com.broll.gainea.client.game.map;

public final class Ship extends Location {
    private Location from;
    private Location to;
    public Ship(int number, String name, Coordinates coordinates) { super(number, name, coordinates); }
    public Location getFrom() { return from; }
    public Location getTo() { return to; }
    public void connect(Location from, Location to) {
        this.from = from; this.to = to;
        getConnectedLocations().add(from); getConnectedLocations().add(to);
        from.getConnectedLocations().add(this); to.getConnectedLocations().add(this);
    }
    @Override public String toString() { return "Schiff"; }
}
