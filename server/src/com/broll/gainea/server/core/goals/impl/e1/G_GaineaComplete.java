package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_GaineaComplete extends OccupyGoal {
    public G_GaineaComplete() {
        super(GoalDifficulty.HARD, "Erobere Gainea und das Totemgebirge/Insel");
    }

    @Override
    protected void initOccupations() {
        occupy(GaineaMap.Continents.GAINEA);
        occupy(GaineaMap.Islands.TOTEMINSEL);
    }

}
