package com.broll.gainea.server.core.goals;

import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.IslandID;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class OccupyGoal extends AbstractGoal {

    private final static Logger Log = LoggerFactory.getLogger(OccupyGoal.class);
    private List<Location> locations = new ArrayList<>();
    private Map<Location, Function<Location, Boolean>> conditions = new HashMap<>();
    protected MapContainer map;

    public OccupyGoal(GoalDifficulty difficulty, String text) {
        super(difficulty, text);
    }

    @Override
    protected boolean validForGame() {
        map = game.getMap();
        initOccupations();
        //if any area is null, then its not part of the expansions, so goal is invalid
        for (Location area : locations) {
            if (area == null) {
                return false;
            }
        }
        if (locations.isEmpty()) {
            //no locations -> missing expansion
            return false;
        }
        //set expansion restrictions by required locations
        setExpansionRestriction(Arrays.stream(ExpansionType.values()).filter(this::hasLocationsOf).toArray(ExpansionType[]::new));
        return true;
    }

    private boolean hasLocationsOf(ExpansionType type) {
        for (Location location : locations) {
            Expansion expansion = map.getExpansion(type);
            if (expansion != null && expansion.getAllLocations().contains(location)) {
                return true;
            }
        }
        return false;
    }

    protected abstract void initOccupations();

    protected List<Area> occupy(AreaID... areas) {
        List<Area> list = Arrays.stream(areas).map(map::getArea).filter(Objects::nonNull).collect(Collectors.toList());
        this.locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, AreaID... areas) {
        List<Area> list = Arrays.stream(areas).map(map::getArea).filter(Objects::nonNull).collect(Collectors.toList());
        list.removeIf(it -> !filter.apply(it));
        this.locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(IslandID... islands) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(islands).map(map::getIsland).filter(Objects::nonNull).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, IslandID... islands) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(islands).map(map::getIsland).filter(Objects::nonNull).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        list.removeIf(it -> !filter.apply(it));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(ContinentID... continents) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(continents).map(map::getContinent).filter(Objects::nonNull).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, ContinentID... continents) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(continents).map(map::getContinent).filter(Objects::nonNull).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        list.removeIf(it -> !filter.apply(it));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(ExpansionType... expansions) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(expansions).map(map::getExpansion).filter(Objects::nonNull).map(Expansion::getContents).forEach(col -> col.stream().map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas)));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, ExpansionType... expansions) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(expansions).map(map::getExpansion).filter(Objects::nonNull).map(Expansion::getContents).forEach(col -> col.stream().map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas)));
        list.removeIf(it -> !filter.apply(it));
        locations.addAll(list);
        return list;
    }

    protected List<Location> occupy(List<Location> locations) {
        this.locations.addAll(locations);
        return locations;
    }

    protected List<Location> occupy(Location... locations) {
        this.locations.addAll(Arrays.asList(locations));
        return Arrays.asList(locations);
    }

    protected List<Location> occupy(Stream<? extends Location> stream) {
        List<Location> list = stream.collect(Collectors.toList());
        this.locations.addAll(list);
        return list;
    }

    protected void condition(List<Location> locations, Function<Location, Boolean>... conditions) {
        locations.forEach(location -> this.conditions.put(location, loc ->
                Arrays.stream(conditions).map(it -> it.apply(loc)).reduce(true, Boolean::logicalAnd)
        ));
    }

    protected Function<Location, Boolean> minPlayerUnitCount(int count) {
        return location -> {
            long playerUnits = location.getInhabitants().stream().filter(it -> it.getOwner() == player && it instanceof BattleObject).count();
            return playerUnits >= count;
        };
    }

    @Override
    public void check() {
        Log.trace("check occupy goal (" + this + ") for player " + player);
        List<Location> occupiedLocations = player.getControlledLocations();
        for (Location location : locations) {
            Function<Location, Boolean> condition = conditions.get(location);
            if (condition != null) {
                if (!condition.apply(location)) {
                    //condition for the location not satisfied
                    return;
                }
            } else {
                //default condition: simply occupied
                if (!occupiedLocations.contains(location)) {
                    //area not occupied by player
                    return;
                }
            }
        }
        success();
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        if (units.get(0).getOwner() == player) {
            //unit of this player moved, check occupy condition
            check();
        }
    }

    @Override
    public void spawned(MapObject object, Location location) {
        if (object.getOwner() == player) {
            //unit of this player spawned, check occupy condition
            check();
        }
    }
}
