package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_AllDeserts extends AbstractOccupyGoal {
    public G_AllDeserts() {
        super(GoalDifficulty.MEDIUM, "Erobere alle Wüsten");
    }

    @Override
    protected void initOccupations() {
        occupy(area -> area.getType() == AreaType.DESERT, GaineaMap.Areas.values());
    }

}
