package com.broll.gainea.server.core.goals.impl.ex;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.map.impl.IcelandMap;

public class G_MoorAndTotem extends OccupyGoal {
    public G_MoorAndTotem() {
        super(GoalDifficulty.MEDIUM, "Erobere die Kontinente Moor und Totem");
    }

    @Override
    protected void initOccupations() {
        occupy(IcelandMap.Continents.TOTEM);
        occupy(GaineaMap.Continents.MOOR);
    }

}
