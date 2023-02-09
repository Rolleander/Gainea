package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.GodDragon;

public class G_Goddrake extends Goal {

    public G_Goddrake() {
        super(GoalDifficulty.EASY, "Töte den Götterdrachen");
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.getWinnerPlayer() == player) {
            if (result.getLoserUnits().stream().anyMatch(it -> it instanceof GodDragon && it.isDead())) {
                success();
            }
        }
    }

    @Override
    public void check() {

    }
}
