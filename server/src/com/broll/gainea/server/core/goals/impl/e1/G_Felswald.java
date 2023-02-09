package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_Felswald extends OccupyGoal {
    public G_Felswald() {
        super(GoalDifficulty.EASY, "Erobere Felswald und Grünland, sowie alle deren benachbarten Länder");
    }

    @Override
    protected void initOccupations() {
        occupy(GaineaMap.Areas.FELSWALD, GaineaMap.Areas.FELSENWUESTE, GaineaMap.Areas.KUESTENGEBIET, GaineaMap.Areas.GRUENLAND, GaineaMap.Areas.XOMDELTA, GaineaMap.Areas.WEIDESTEPPE);
    }

}
