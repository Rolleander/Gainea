package com.broll.gainea.server.core.map;

import com.broll.gainea.client.ui.render.ExpansionRender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ExpansionFactory {

    private Expansion expansion;
    protected Coordinates baseCoordinates;
    private List<ExpansionFactory> connectedExpansions = new ArrayList<>();
    private Map<AreaID, Area> areas = new HashMap<>();
    private Map<ContinentID, Continent> continents = new HashMap<>();
    private Map<IslandID, Island> islands = new HashMap<>();

    public ExpansionFactory(ExpansionType type) {
        expansion = new Expansion();
        expansion.setType(type);
        setBaseCoordinates(0, 0);
    }

    protected void setBaseCoordinates(float x, float y) {
        baseCoordinates = new Coordinates(x, y);
        expansion.setCoordinates(baseCoordinates);
    }

    protected Coordinates coord(float x, float y) {
        Coordinates c = new Coordinates(x / 100f, y / 100f);
        c.shift(baseCoordinates.getX(), baseCoordinates.getY());
        return c;
    }

    protected void connect(AreaID... areas) {
        for (AreaID a1 : areas) {
            for (AreaID a2 : areas) {
                if (a1 != a2) {
                    Area area1 = get(a1);
                    Area area2 = get(a2);
                    area1.addAdjacentLocation(area2);
                }
            }
        }
    }

    public abstract ExpansionRender createRender();

    protected abstract void init();

    protected abstract void connectWithExpansion(ExpansionFactory expansion);

    public final void connectExpansion(ExpansionFactory expansion) {
        this.connectedExpansions.add(expansion);
        this.connectWithExpansion(expansion);
    }

    protected IslandID island(IslandID id, String name, List<AreaID> areaIds) {
        Island island = new Island(id);
        List<Area> areas = areaIds.stream().map(this::get).collect(Collectors.toList());
        areas.forEach(area -> area.setContainer(island));
        island.setAreas(areas);
        island.setName(name);
        this.islands.put(id, island);
        return id;
    }

    protected ContinentID continent(ContinentID id, String name, List<AreaID> areaIds) {
        Continent continent = new Continent(id);
        List<Area> areas = areaIds.stream().map(this::get).collect(Collectors.toList());
        areas.forEach(area -> area.setContainer(continent));
        continent.setAreas(areas);
        continent.setName(name);
        this.continents.put(id, continent);
        return id;
    }

    protected AreaID area(AreaID id, String name, AreaType type, float x, float y) {
        Area area = new Area(id);
        area.setName(name);
        area.setType(type);
        area.setCoordinates(coord(x, y));
        this.areas.put(id, area);
        return id;
    }

    private Ship ship(Location from, Location to, float x, float y) {
        Ship ship = new Ship();
        ship.setFrom(from);
        ship.setTo(to);
        if (from instanceof Area) {
            ((Area) from).addAdjacentLocation(ship);
        }
        if (to instanceof Area) {
            ((Area) to).addAdjacentLocation(ship);
        }
        ship.setCoordinates(coord(x, y));
        return ship;
    }

    protected Ship ship(AreaID from, AreaID to, float x, float y) {
        Ship ship = ship(get(from), get(to), x, y);
        get(from).getContainer().getShips().add(ship);
        return ship;
    }

    protected List<Ship> ships(AreaID fromId, AreaID toId, float[] x, float[] y) {
        List<Ship> ships = new ArrayList<>();
        Location current = get(fromId);
        Area to = get(toId);
        for (int i = 0; i < x.length; i++) {
            ships.add(ship(current, null, x[i], y[i]));
            current = ships.get(i);
        }
        for (int i = 0; i < x.length; i++) {
            Location next = to;
            if (i < x.length - 1) {
                next = ships.get(i + 1);
            } else {
                to.addAdjacentLocation(ships.get(i));
            }
            ships.get(i).setTo(next);
        }
        get(fromId).getContainer().getShips().addAll(ships);
        return ships;
    }

    protected <T> List<T> list(T... t) {
        return Arrays.asList(t);
    }

    public Expansion create() {
        List<AreaCollection> contents = new ArrayList<>();
        init();
        contents.addAll(islands.values());
        contents.addAll(continents.values());
        expansion.setContents(contents);
        return expansion;
    }

    private Area get(AreaID id) {
        Area area = areas.get(id);
        if (area != null) {
            return area;
        } else {
            for (ExpansionFactory e : connectedExpansions) {
                area = e.areas.get(id);
                if (area != null) {
                    return area;
                }
            }
        }
        return null;
    }

    private Continent get(ContinentID id) {
        Continent area = continents.get(id);
        if (area != null) {
            return area;
        } else {
            for (ExpansionFactory e : connectedExpansions) {
                area = e.continents.get(id);
                if (area != null) {
                    return area;
                }
            }
        }
        return null;
    }

    private Island get(IslandID id) {
        Island area = islands.get(id);
        if (area != null) {
            return area;
        } else {
            for (ExpansionFactory e : connectedExpansions) {
                area = e.islands.get(id);
                if (area != null) {
                    return area;
                }
            }
        }
        return null;
    }
}
