package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_AllDeserts extends OccupyGoal {
    public G_AllDeserts() {
        super(GoalDifficulty.MEDIUM, "Erobere alle Wüsten");
    }

    @Override
    protected void initOccupations() {
        occupy(area -> area.getType() == AreaType.DESERT, GaineaMap.Areas.values());
    }

}
