package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;

public class G_Travel extends CustomOccupyGoal {


    public G_Travel() {
        super(GoalDifficulty.MEDIUM, "Kontrolliere Einheiten auf 5 verschiedenen Landmassen");
        setProgressionGoal(5);
    }

    @Override
    public void check() {
        int containers = (int) player.getControlledLocations().stream().filter(it -> !(it instanceof Ship)).map(Location::getContainer).distinct().count();
        updateProgression(containers);
        if (containers >= 5) {
            success();
        }
    }
}
