package com.broll.gainea.server.core.goals.impl.e3;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.impl.BoglandMap;

public class G_IslandsAndLakes extends OccupyGoal {
    public G_IslandsAndLakes() {
        super(GoalDifficulty.MEDIUM, "Besetze alle Inseln und Seen");
    }

    @Override
    protected void initOccupations() {
        occupy(BoglandMap.Islands.values());
        occupy(it -> it.getType() == AreaType.LAKE, ExpansionType.BOGLANDS);
    }

}
