package com.broll.gainea.server.core.map;

import com.broll.gainea.client.render.ExpansionRender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ExpansionFactory {

    private Expansion expansion;
    protected Coordinates baseCoordinates;

    public ExpansionFactory(ExpansionType type) {
        expansion = new Expansion();
        expansion.setType(type);
       setBaseCoordinates(0,0);
    }

    protected void setBaseCoordinates(float x, float y){
        baseCoordinates = new Coordinates(x,y);
        expansion.setCoordinates(baseCoordinates);
    }

    protected Coordinates coord(float x, float y) {
        Coordinates c= new Coordinates(x/100f,y/100f);
        c.shift(baseCoordinates.getX(1),baseCoordinates.getY(1));
        return c;
    }

    public abstract ExpansionRender createRender();

    protected abstract void init(List<AreaCollection> contents);

    protected abstract void connectExpansion(ExpansionFactory expansion);

    protected Island island(String name, List<Area> areas) {
        Island island = new Island();
        areas.forEach(area->area.setContainer(island));
        island.setAreas(areas);
        island.setName(name);
        return island;
    }

    protected Continent continent(String name, List<Area> areas) {
        Continent continent = new Continent();
        areas.forEach(area->area.setContainer(continent));
        continent.setAreas(areas);
        continent.setName(name);
        return continent;
    }

    protected Area area(String name, AreaType type, float x, float y) {
        Area area = new Area();
        area.setName(name);
        area.setType(type);
        area.setCoordinates(coord(x, y));
        return area;
    }

    protected Ship ship(Location from, Location to, float x, float y) {
        Ship ship = new Ship();
        ship.setFrom(from);
        ship.setTo(to);
        if(from instanceof Area){
            ((Area)from).addAdjacentLocation(ship);
        }
        if(to instanceof Area){
            ((Area)to).addAdjacentLocation(ship);
        }
        ship.setCoordinates(coord(x, y));
        return ship;
    }

    protected List<Ship> ships(Location from, Location to, float[] x, float[] y) {
        List<Ship> ships = new ArrayList<>();
        Location current = from;
        for (int i = 0; i < x.length; i++) {
            ships.add(ship(current, null, x[i], y[i]));
            current = ships.get(i);
        }
        for (int i = 0; i < x.length; i++) {
            Location next = to;
            if (i < x.length - 1) {
                next = ships.get(i + 1);
            }
            else{
                if(to instanceof Area){
                    ((Area)to).addAdjacentLocation(ships.get(i));
                }
            }
            ships.get(i).setTo(next);
        }
        return ships;
    }

    protected <T> List<T> list(T... t) {
        return Arrays.asList(t);
    }

    public Expansion create() {
        List<AreaCollection> contents = new ArrayList<>();
        init(contents);
        expansion.setContents(contents);
        return expansion;
    }

}
