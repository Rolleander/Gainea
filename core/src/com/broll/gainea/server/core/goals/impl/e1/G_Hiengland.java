package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.Gainea;
import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_Hiengland extends AbstractOccupyGoal {
    public G_Hiengland() {
        super(GoalDifficulty.EASY, "Erobere Hiengland und alle angrenzenden Areale");
    }

    @Override
    protected void initOccupations() {
        occupy(GaineaMap.Areas.HIENGLAND, GaineaMap.Areas.WEIDESTEPPE, GaineaMap.Areas.DUNKLESMEER, GaineaMap.Areas.KIESSTRAND, GaineaMap.Areas.ZWINGSEE, GaineaMap.Areas.KORBERG);
    }

}
