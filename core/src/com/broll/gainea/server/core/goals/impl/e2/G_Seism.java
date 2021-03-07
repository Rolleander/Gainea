package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.IcelandMap;

public class G_Seism extends OccupyGoal {
    public G_Seism() {
        super(GoalDifficulty.EASY, "Erobere Seism");
    }

    @Override
    protected void initOccupations() {
        occupy(IcelandMap.Continents.SEISM);
    }

}
