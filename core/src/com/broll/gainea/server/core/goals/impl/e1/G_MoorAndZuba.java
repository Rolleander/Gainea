package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_MoorAndZuba extends AbstractOccupyGoal {
    public G_MoorAndZuba() {
        super(GoalDifficulty.HARD, "Erobere den Kontinent Moor und Zuba");
    }

    @Override
    protected void initOccupations() {
        occupy(GaineaMap.Continents.MOOR);
        occupy(GaineaMap.Continents.ZUBA);
    }

}
