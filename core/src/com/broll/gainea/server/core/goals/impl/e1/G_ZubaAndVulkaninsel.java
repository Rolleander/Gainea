package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_ZubaAndVulkaninsel extends AbstractOccupyGoal {
    public G_ZubaAndVulkaninsel() {
        super(GoalDifficulty.MEDIUM, "Erobere den Kontinent Zuba und die Vulkaninsel mit Vulkanberg");
    }

    @Override
    protected void initOccupations() {
        occupy(GaineaMap.Continents.ZUBA);
        occupy(GaineaMap.Islands.VULKANINSEL);
    }

}
