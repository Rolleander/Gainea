package com.broll.gainea.server.core.map;

import java.util.ArrayList;
import java.util.List;

public abstract class AreaCollection {
    private Expansion container;
    private List<Area> areas = new ArrayList<>();
    private List<Ship> ships = new ArrayList<>();
    private String name;

    public String getName() {
        return name;
    }

    public void init(Expansion container) {
        this.container = container;
    }

    public Area getArea(AreaID id) {
        return areas.stream().filter(it -> it.getId() == id).findFirst().orElse(null);
    }

    public Expansion getExpansion() {
        return container;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public List<Ship> getShips() {
        return ships;
    }
}
