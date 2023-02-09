package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;

public class G_AllPlains extends OccupyGoal {
    public G_AllPlains() {
        super(GoalDifficulty.EASY, "Erobere alle Steppen");
    }

    @Override
    protected void initOccupations() {
        occupy(it -> it.getType() == AreaType.PLAINS, ExpansionType.ICELANDS);
    }

}
