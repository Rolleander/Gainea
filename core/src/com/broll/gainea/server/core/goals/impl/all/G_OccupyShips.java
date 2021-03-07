package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import java.util.List;

public class G_OccupyShips extends CustomOccupyGoal {
    private final int ships;

    public G_OccupyShips() {
        this(GoalDifficulty.EASY, 6);
    }

    public G_OccupyShips(GoalDifficulty difficulty, int ships) {
        super(difficulty, "Erobere " + ships + " Schiffe");
        this.ships = ships;
    }

    @Override
    public void check() {
        if (player.getControlledLocations().stream().filter(it -> it instanceof Ship).count() >= ships) {
            success();
        }
    }

}