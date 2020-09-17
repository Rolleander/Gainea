package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import java.util.List;

public class G_OccupyShips extends AbstractGoal {
    private final static int SHIPS = 7;

    public G_OccupyShips() {
        super(GoalDifficulty.MEDIUM, "Erobere " + SHIPS + " Schiffe");
    }

    private void check() {
        if (player.getControlledLocations().stream().filter(it -> it instanceof Ship).count() >= SHIPS) {
            success();
        }
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        if (location instanceof Ship) {
            check();
        }
    }
}