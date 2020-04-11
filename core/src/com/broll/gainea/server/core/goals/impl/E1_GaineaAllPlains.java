package com.broll.gainea.server.core.goals.impl;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.impl.GaineaMap;

import java.util.stream.Stream;

public class E1_GaineaAllPlains extends AbstractOccupyGoal {
    public E1_GaineaAllPlains() {
        super(GoalDifficulty.EASY, "Erobere alle Steppen auf Gainea");
    }

    @Override
    protected void initOccupations() {
        occupy(it -> it.getType() == AreaType.PLAINS, GaineaMap.Continents.GAINEA);
    }

}
