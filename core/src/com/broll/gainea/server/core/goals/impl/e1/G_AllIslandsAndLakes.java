package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.impl.GaineaMap;

public class G_AllIslandsAndLakes extends AbstractOccupyGoal {
    public G_AllIslandsAndLakes() {
        super(GoalDifficulty.HARD, "Erobere alle Inseln und Meere");
    }

    @Override
    protected void initOccupations() {
        occupy(it -> it.getType() == AreaType.LAKE, ExpansionType.GAINEA);
        occupy(GaineaMap.Islands.values());
    }

}
