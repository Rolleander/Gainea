package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.utils.LocationUtils;

public class G_AllMountainsAndLakes extends OccupyGoal {
    public G_AllMountainsAndLakes() {
        super(GoalDifficulty.HARD, "Erobere alle Berge und Seen");
    }

    @Override
    protected void initOccupations() {
        occupy(it -> LocationUtils.isAreaType(it, AreaType.LAKE, AreaType.MOUNTAIN), ExpansionType.ICELANDS);
    }

}
