package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Area;

public class G_AnyAreas extends CustomOccupyGoal {
    private int count =0;

    public G_AnyAreas(){
        this(GoalDifficulty.EASY, 7);
    }

    public G_AnyAreas(GoalDifficulty difficulty, int count) {
        super(difficulty, "Besetze "+count+" beliebige Gebiete");
        this.count = count;
        this.setProgressionGoal(count);
    }

    @Override
    public void check() {
        int current = (int) player.getControlledLocations().stream().filter(it-> it instanceof Area).count();
        updateProgression(current);
        if(current>=count){
            success();
        }
    }
}
