package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_AllIslands extends AbstractOccupyGoal {
    public G_AllIslands() {
        super(GoalDifficulty.MEDIUM, "Erobere alle Inseln");
    }

    @Override
    protected void initOccupations() {
        occupy(GaineaMap.Islands.values());
    }

}
