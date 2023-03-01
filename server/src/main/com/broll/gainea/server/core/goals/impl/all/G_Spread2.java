package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.MissingExpansionException;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Continent;
import com.broll.gainea.server.core.map.Location;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class G_Spread2 extends OccupyGoal {

    private final static int LOCATIONS = 5;

    public G_Spread2() {
        super(GoalDifficulty.HARD, null);
    }

    @Override
    protected void initOccupations() {
        List<Continent> continents = game.getMap().getAllContinents();
        Collections.shuffle(continents);
        List<Area> locations = continents.stream().limit(LOCATIONS).map(it ->
                RandomUtils.pickRandom(it.getAreas())).collect(Collectors.toList());
        if (locations.size() < LOCATIONS) {
            throw new MissingExpansionException();
        }
        occupy(locations.stream().map(it -> (Location) it).collect(Collectors.toList()));
        this.text = "Erobere " + locations.stream().map(Area::getName).collect(Collectors.joining(","));
    }

}
