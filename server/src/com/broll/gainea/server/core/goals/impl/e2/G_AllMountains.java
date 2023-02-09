package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;

public class G_AllMountains extends OccupyGoal {
    public G_AllMountains() {
        super(GoalDifficulty.EASY, "Erobere alle Berge");
    }

    @Override
    protected void initOccupations() {
        occupy(it -> it.getType() == AreaType.MOUNTAIN, ExpansionType.ICELANDS);
    }

}
