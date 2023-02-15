package com.broll.gainea.server.core.goals;

import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.IslandID;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.MapContainer;
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

public abstract class OccupyGoal extends Goal {

    private final static Logger Log = LoggerFactory.getLogger(OccupyGoal.class);
    private List<Location> locations = new ArrayList<>();
    private Map<Location, Function<Location, Boolean>> conditions = new HashMap<>();
    protected MapContainer map;

    public OccupyGoal(GoalDifficulty difficulty, String text) {
        super(difficulty, text);
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setRequiredUnits(locations.size());
        strategy.updateTargets(locations.stream().collect(Collectors.toSet()));
    }

    @Override
    protected boolean validForGame() {
        map = game.getMap();
        try {
            initOccupations();
        } catch (MissingExpansionException e) {
            return false;
        }
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
        setProgressionGoal(locations.size());
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

    private void assureIslandExists(IslandID... islands) {
        for (IslandID island : islands) {
            if (map.getIsland(island) == null) {
                throw new MissingExpansionException();
            }
        }
    }

    private void assureContinentExists(ContinentID... continents) {
        for (ContinentID continent : continents) {
            if (map.getContinent(continent) == null) {
                throw new MissingExpansionException();
            }
        }
    }

    private void assureExpansionExists(ExpansionType... expansions) {
        for (ExpansionType expansion : expansions) {
            if (map.getExpansion(expansion) == null) {
                throw new MissingExpansionException();
            }
        }
    }

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
        assureIslandExists(islands);
        List<Area> list = new ArrayList<>();
        Arrays.stream(islands).map(map::getIsland).filter(Objects::nonNull).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, IslandID... islands) {
        assureIslandExists(islands);
        List<Area> list = new ArrayList<>();
        Arrays.stream(islands).map(map::getIsland).filter(Objects::nonNull).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        list.removeIf(it -> !filter.apply(it));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(ContinentID... continents) {
        assureContinentExists(continents);
        List<Area> list = new ArrayList<>();
        Arrays.stream(continents).map(map::getContinent).filter(Objects::nonNull).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, ContinentID... continents) {
        assureContinentExists(continents);
        List<Area> list = new ArrayList<>();
        Arrays.stream(continents).map(map::getContinent).filter(Objects::nonNull).map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas));
        list.removeIf(it -> !filter.apply(it));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(ExpansionType... expansions) {
        assureExpansionExists(expansions);
        List<Area> list = new ArrayList<>();
        Arrays.stream(expansions).map(map::getExpansion).filter(Objects::nonNull).map(Expansion::getContents).forEach(col -> col.stream().map(AreaCollection::getAreas).forEach(areas -> list.addAll(areas)));
        locations.addAll(list);
        return list;
    }

    protected List<Area> occupy(Function<Area, Boolean> filter, ExpansionType... expansions) {
        assureExpansionExists(expansions);
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

    @Override
    public void check() {
        List<Location> occupiedLocations = player.getControlledLocations();
        boolean success = true;
        int progress = 0;
        for (Location location : locations) {
            Function<Location, Boolean> condition = conditions.get(location);
            if (condition != null) {
                if (condition.apply(location)) {
                    progress++;
                } else {
                    //condition for the location not satisfied
                    success = false;
                }
            } else {
                //default condition: simply occupied
                if (occupiedLocations.contains(location)) {
                    progress++;
                } else {
                    //area not occupied by player
                    success = false;
                }
            }
        }
        updateProgression(progress);
        if (success) {
            success();
        }
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
