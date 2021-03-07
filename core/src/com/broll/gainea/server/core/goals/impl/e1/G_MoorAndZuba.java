package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_MoorAndZuba extends OccupyGoal {
    public G_MoorAndZuba() {
        super(GoalDifficulty.HARD, "Erobere den Kontinent Moor und Zuba");
    }

    @Override
    protected void initOccupations() {
        occupy(GaineaMap.Continents.MOOR);
        occupy(GaineaMap.Continents.ZUBA);
    }

}
