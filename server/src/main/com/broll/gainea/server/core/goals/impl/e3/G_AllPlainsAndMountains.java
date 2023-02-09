package com.broll.gainea.server.core.goals.impl.e3;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.impl.BoglandMap;

public class G_AllPlainsAndMountains extends OccupyGoal {
    public G_AllPlainsAndMountains() {
        super(GoalDifficulty.HARD, "Erobere alle Steppen und Berge");
    }

    @Override
    protected void initOccupations() {
        occupy(area -> area.getType() == AreaType.PLAINS || area.getType() == AreaType.MOUNTAIN, BoglandMap.Areas.values());
    }

}
