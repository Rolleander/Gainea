package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.RoundGoal;

public class G_Fight extends RoundGoal {

    private boolean fighting = false;

    public G_Fight() {
        super(GoalDifficulty.EASY, "Sei für 5 aufeinanderfolgende Runden an Kämpfen beteiligt", 5);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.isParticipating(player)) {
            fighting = true;
        }
    }

    @Override
    public void check() {
        if (fighting) {
            progressRound();
        } else {
            resetRounds();
        }
        fighting = false;
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setPrepareStrategy(() -> {
            strategy.updateTargets(BotUtils.huntOtherPlayersTargets(player, game));
            strategy.setRequiredUnits(5);
        });
    }
}
