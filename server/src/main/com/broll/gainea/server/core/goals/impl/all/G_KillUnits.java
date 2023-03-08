package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.Soldier;

public class G_KillUnits extends Goal {
    private final int killTarget;
    private int kills = 0;

    public G_KillUnits() {
        this(GoalDifficulty.EASY, 6);
    }

    public G_KillUnits(GoalDifficulty difficulty, int kills) {
        super(difficulty, "Vernichte " + kills + " Soldaten anderer Spieler durch Kämpfe");
        this.killTarget = kills;
        setProgressionGoal(kills);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.isAttacker(player)) {
            kills += result.getKilledDefenders().stream().filter(it -> it instanceof Soldier).count();
            check();
        } else if (result.isDefender(player)) {
            kills += result.getKilledAttackers().stream().filter(it -> it instanceof Soldier).count();
            check();
        }
    }

    @Override
    public void check() {
        updateProgression(kills);
        if (kills >= killTarget) {
            success();
        }
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setSpreadUnits(false);
        strategy.setPrepareStrategy(() -> {
            strategy.updateTargets(BotUtils.huntOtherPlayersTargets(player, game));
            strategy.setRequiredUnits(killTarget - kills);
        });
    }
}
