package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;

public class G_Survive extends Goal {
    private final static int TARGET = 5;
    private int turns = 0;

    public G_Survive() {
        super(GoalDifficulty.EASY, "Verliere für " + TARGET + " Runden keine Einheit");
        setProgressionGoal(TARGET);
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if (unit.getOwner() == player) {
            turns = 0;
            updateProgression(turns);
        }
    }

    @Override
    public void turnStarted(Player player) {
        if (player == this.player && game.getRounds() > 1) {
            turns++;
            check();
        }
    }

    @Override
    public void check() {
        updateProgression(turns);
        if (turns > TARGET) {
            success();
        }
    }
}
