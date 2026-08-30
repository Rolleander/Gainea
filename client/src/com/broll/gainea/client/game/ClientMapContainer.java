package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.map.*;
import com.broll.gainea.client.ui.ingame.map.ExpansionDebugRender;
import com.broll.gainea.client.ui.ingame.map.ExpansionRender;
import com.broll.gainea.client.ui.ingame.map.MapLabel;
import com.broll.gainea.client.ui.ingame.map.WaterRender;
import com.broll.gainea.net.proto.NT_Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Client-side render topology reconstructed from the server's protobuf snapshot. */
public class ClientMapContainer {
    public static boolean RENDER_DEBUG = false;
    private final Gainea game;
    private final List<Expansion> expansions = new ArrayList<>();
    private final List<ExpansionRender> renders = new ArrayList<>();
    private final Map<Integer, Location> locations = new HashMap<>();

    public ClientMapContainer(Gainea game, List<com.broll.gainea.net.proto.NT_Expansion> topology) {
        this.game = game;
        build(topology);
    }

    private void build(List<com.broll.gainea.net.proto.NT_Expansion> topology) {
        for (com.broll.gainea.net.proto.NT_Expansion source : topology) {
            Expansion expansion = new Expansion(source.getName(), source.getTexture(),
                new Coordinates(source.getX(), source.getY()));
            expansions.add(expansion);
            for (com.broll.gainea.net.proto.NT_LocationContainer sourceContainer : source.getLocationContainersList()) {
                AreaCollection container = new AreaCollection(sourceContainer.getName());
                expansion.getContents().add(container);
                for (NT_Location sourceLocation : sourceContainer.getLocationsList()) {
                    Area area = new Area(sourceLocation.getNumber(), sourceLocation.getName(), coordinates(sourceLocation), sourceLocation.getAreaType());
                    addLocation(area);
                    container.add(area);
                }
            }
            for (com.broll.gainea.net.proto.NT_Ship sourceShip : source.getShipsList()) {
                NT_Location sourceLocation = sourceShip.getLocation();
                Ship ship = new Ship(sourceLocation.getNumber(), sourceLocation.getName(), coordinates(sourceLocation));
                addLocation(ship);
                expansion.getAllShips().add(ship);
            }
        }

        int expansionIndex = 0;
        for (com.broll.gainea.net.proto.NT_Expansion source : topology) {
            Expansion expansion = expansions.get(expansionIndex++);
            for (int i = 0; i < source.getShipsCount(); i++) {
                com.broll.gainea.net.proto.NT_Ship sourceShip = source.getShips(i);
                Location from = requiredLocation(sourceShip.getFrom());
                Location to = requiredLocation(sourceShip.getTo());
                expansion.getAllShips().get(i).connect(from, to);
            }
        }

        for (Expansion expansion : expansions) {
            ExpansionRender render = new ExpansionRender(expansion.getTexture());
            render.init(game, expansion);
            renders.add(render);
        }
    }

    private Coordinates coordinates(NT_Location location) {
        return new Coordinates(location.getX(), location.getY());
    }

    private void addLocation(Location location) {
        Location previous = locations.put(location.getNumber(), location);
        if (previous != null) throw new IllegalArgumentException("Duplicate map location " + location.getNumber());
    }

    private Location requiredLocation(int number) {
        Location location = locations.get(number);
        if (location == null) throw new IllegalArgumentException("Map references unknown location " + number);
        return location;
    }

    public Location getLocation(int number) { return requiredLocation(number); }
    public Area getArea(String name) {
        for (Expansion expansion : expansions) for (Area area : expansion.getAllAreas()) if (area.getName().equals(name)) return area;
        return null;
    }
    public List<Expansion> getExpansions() { return expansions; }
    public List<ExpansionRender> getRenders() { return renders; }

    public void displayRenders() {
        game.gameStage.addActor(new WaterRender(game));
        for (ExpansionRender render : renders) game.gameStage.addActor(render);
        for (Expansion expansion : expansions) {
            for (Area area : expansion.getAllAreas()) {
                MapLabel label = new MapLabel(game, area.getName());
                label.setPosition(area.getCoordinates().getDisplayX(), area.getCoordinates().getDisplayY());
                game.gameStage.addActor(label);
            }
            if (RENDER_DEBUG) game.gameStage.addActor(new ExpansionDebugRender(game, expansion));
        }
    }
}
