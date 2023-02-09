package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.IcelandMap;

public class G_Teron extends OccupyGoal {
    public G_Teron() {
        super(GoalDifficulty.MEDIUM, "Erobere Teron");
    }

    @Override
    protected void initOccupations() {
        occupy(IcelandMap.Continents.TERON);
    }

}
