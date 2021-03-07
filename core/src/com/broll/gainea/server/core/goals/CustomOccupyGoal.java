package com.broll.gainea.server.core.goals;

import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;

import java.util.List;

public abstract class CustomOccupyGoal extends AbstractGoal {
    public CustomOccupyGoal(GoalDifficulty difficulty, String text) {
        super(difficulty, text);
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        if (units.get(0).getOwner() == player) {
            check();
        }
    }

    @Override
    public void spawned(MapObject object, Location location) {
        if (object.getOwner() == player) {
            check();
        }
    }
}
