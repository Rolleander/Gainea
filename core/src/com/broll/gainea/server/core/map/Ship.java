package com.broll.gainea.server.core.map;

import java.util.HashSet;
import java.util.Set;

public class Ship extends Location {

    private Location from;
    private Location to;
    private Set<Location> connected = new HashSet<>();

    public void setFrom(Location from) {
        this.from = from;
    }

    public void setTo(Location to) {
        this.to = to;
        connected.clear();
        connected.add(to);
    }

    public boolean passable(Location from) {
        if (this.from == from) {
            return true;
        }
        return false;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    @Override
    public Set<Location> getConnectedLocations() {
        return connected;
    }
}
