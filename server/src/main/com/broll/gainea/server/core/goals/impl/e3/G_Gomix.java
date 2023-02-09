package com.broll.gainea.server.core.goals.impl.e3;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.impl.BoglandMap;

public class G_Gomix extends OccupyGoal {
    public G_Gomix() {
        super(GoalDifficulty.MEDIUM, "Erobere den Kontinent Gomix");
    }

    @Override
    protected void initOccupations() {
        occupy(BoglandMap.Continents.GOMIX);
    }
}
