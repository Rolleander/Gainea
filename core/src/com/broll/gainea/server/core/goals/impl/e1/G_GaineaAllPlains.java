package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_GaineaAllPlains extends OccupyGoal {
    public G_GaineaAllPlains() {
        super(GoalDifficulty.EASY, "Erobere alle Steppen auf Gainea");
    }

    @Override
    protected void initOccupations() {
        occupy(it -> it.getType() == AreaType.PLAINS, GaineaMap.Continents.GAINEA);
    }

}
