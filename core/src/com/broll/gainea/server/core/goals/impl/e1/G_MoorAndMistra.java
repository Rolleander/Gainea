package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_MoorAndMistra extends AbstractOccupyGoal {
    public G_MoorAndMistra() {
        super(GoalDifficulty.EASY, "Erobere den Kontinent Moor und die Mistra Insel");
    }

    @Override
    protected void initOccupations() {
        occupy(GaineaMap.Continents.MOOR);
        occupy(GaineaMap.Islands.MISTRAINSEL);
    }

}
