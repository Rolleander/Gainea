package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.GodDragon;

public class G_Goddrake extends AbstractGoal {

    public G_Goddrake() {
        super(GoalDifficulty.MEDIUM, "Töte den Götterdrachen");
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
