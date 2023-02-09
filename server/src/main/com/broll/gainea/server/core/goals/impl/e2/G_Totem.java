package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.IcelandMap;

public class G_Totem extends OccupyGoal {
    public G_Totem() {
        super(GoalDifficulty.EASY, "Erobere Totem und Weißes Gebiet");
    }

    @Override
    protected void initOccupations() {
        occupy(IcelandMap.Continents.TOTEM);
        occupy(IcelandMap.Areas.WEISSESGEBIET);
    }

}
