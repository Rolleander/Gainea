package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;

public class G_Snow extends CustomOccupyGoal {

    private final static int COUNT = 5;

    public G_Snow() {
        super(GoalDifficulty.EASY, "Erobere " + COUNT + " Schneegebiete");
        setExpansionRestriction(ExpansionType.ICELANDS);
    }

    @Override
    public void check() {
        long count = LocationUtils.getControlledLocationsIn(player, ExpansionType.ICELANDS).stream().filter(it -> LocationUtils.isAreaType(it, AreaType.SNOW)).count();
        if (count >= COUNT) {
            success();
        }
    }
}
