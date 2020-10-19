package com.broll.gainea.server.core.goals;

import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.IslandID;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractOccupyGoal extends AbstractGoal {

    private List<Location> locations = new ArrayList<>();
    private Map<Location, Function<Location, Boolean>> conditions = new HashMap<>();
    protected MapContainer map;

    public AbstractOccupyGoal(GoalDifficulty difficulty, String text) {
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
        List<Area> list = Arrays.stream(areas).map(map::getArea).collect(Collectors.toList());
        this.locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(IslandID... islands) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(islands).map(map::getIsland).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, IslandID... islands) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(islands).map(map::getIsland).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        list.removeIf(it -> !filter.apply(it));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(ContinentID... continents) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(continents).map(map::getContinent).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, ContinentID... islands) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(islands).map(map::getContinent).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        list.removeIf(it -> !filter.apply(it));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(ExpansionType... expansions) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(expansions).map(map::getExpansion).map(Expansion::getContents).forEach(col -> col.stream().map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas)));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, ExpansionType... expansions) {
        List<Area> list = new ArrayList<>();
        Arrays.stream(expansions).map(map::getExpansion).map(Expansion::getContents).forEach(col -> col.stream().map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas)));
        list.removeIf(it -> !filter.apply(it));
        locations.addAll(list);
        return list;
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
            long playerUnits = location.getInhabitants().stream().filter(it -> it instanceof BattleObject).map(it -> (BattleObject) it).filter(it -> it.getOwner() == player).count();
            return playerUnits >= count;
        };
    }

    public boolean checkCondition() {
        List<Location> occupiedLocations = player.getControlledLocations();
        for (Location location : occupiedLocations) {
            Function<Location, Boolean> condition = conditions.get(location);
            if (condition != null) {
                if (!condition.apply(location)) {
                    //condition for the location not satisfied
                    return false;
                }
            } else {
                //default condition: simply occupied
                if (!locations.contains(location)) {
                    //area not occupied by player
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        MapObject mo = units.get(0);
        if (mo instanceof BattleObject) {
            if (((BattleObject) mo).getOwner() == player) {
                //unit of this player moved, check occupy condition
                checkCondition();
            }
        }
    }
}
