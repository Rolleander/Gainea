package com.broll.gainea.client.game.map;

import java.util.ArrayList;
import java.util.List;

public final class AreaCollection {
    private final String name;
    private final List<Area> areas = new ArrayList<>();
    public AreaCollection(String name) { this.name = name; }
    public String getName() { return name; }
    public List<Area> getAreas() { return areas; }
    public void add(Area area) { area.setContainer(this); areas.add(area); }
}
