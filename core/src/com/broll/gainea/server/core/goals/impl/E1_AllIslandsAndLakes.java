package com.broll.gainea.server.core.goals.impl;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class E1_AllIslandsAndLakes extends AbstractOccupyGoal {
    public E1_AllIslandsAndLakes() {
        super(GoalDifficulty.HARD, "Erobere alle Inseln und Meere");
    }

    @Override
    protected void initOccupations() {
        occupy(it -> it.getType() == AreaType.LAKE, ExpansionType.GAINEA);
        occupy(GaineaMap.Islands.values());
    }

}
