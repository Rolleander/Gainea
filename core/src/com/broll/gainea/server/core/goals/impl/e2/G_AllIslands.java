package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.IcelandMap;

public class G_AllIslands extends OccupyGoal {
    public G_AllIslands() {
        super(GoalDifficulty.MEDIUM, "Erobere alle Inseln");
    }

    @Override
    protected void initOccupations() {
        occupy(IcelandMap.Islands.values());
    }

}
