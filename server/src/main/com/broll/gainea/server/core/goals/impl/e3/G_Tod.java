package com.broll.gainea.server.core.goals.impl.e3;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.impl.BoglandMap;

public class G_Tod extends OccupyGoal {
    public G_Tod() {
        super(GoalDifficulty.MEDIUM, "Erobere den Kontinent Tod");
    }

    @Override
    protected void initOccupations() {
        occupy(BoglandMap.Continents.TOD);
    }
}
