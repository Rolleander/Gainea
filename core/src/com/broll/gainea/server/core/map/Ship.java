package com.broll.gainea.server.core.map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.set.UnmodifiableSet;

import java.util.HashSet;
import java.util.Set;

public class Ship extends Location {

    private Location from;
    private Location to;

    public void setFrom(Location from) {
        this.from = from;
    }

    public void setTo(Location to) {
        this.to = to;
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
        Set<Location> set = new HashSet<>(2);
        set.add(from);
        set.add(to);
        return set;
    }
}
