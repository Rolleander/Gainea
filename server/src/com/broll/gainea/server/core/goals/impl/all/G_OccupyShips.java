package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Ship;

public class G_OccupyShips extends CustomOccupyGoal {
    private final int ships;

    public G_OccupyShips() {
        this(GoalDifficulty.EASY, 6);
    }

    public G_OccupyShips(GoalDifficulty difficulty, int ships) {
        super(difficulty, "Erobere " + ships + " Schiffe");
        this.ships = ships;
        setProgressionGoal(ships);
    }

    @Override
    public void check() {
        long count = player.getControlledLocations().stream().filter(it -> it instanceof Ship).count();
        updateProgression((int) count);
        if (count >= ships) {
            success();
        }
    }

}