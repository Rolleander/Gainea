package com.broll.gainea.server.core.goals.impl.e3;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;

public class G_AllSwamps extends OccupyGoal {
    public G_AllSwamps() {
        super(GoalDifficulty.MEDIUM, "Erobere alle Sümpfe");
    }

    @Override
    protected void initOccupations() {
        occupy(it -> it.getType() == AreaType.BOG, ExpansionType.BOGLANDS);
    }
}
