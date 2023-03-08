package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;

import java.util.Set;
import java.util.stream.Collectors;

public class G_WinBattles extends Goal {
    private final int winTarget;
    private int wins = 0;

    public G_WinBattles() {
        this(GoalDifficulty.MEDIUM, 6);
    }

    public G_WinBattles(GoalDifficulty difficulty, int wins) {
        super(difficulty, "Gewinne " + wins + " Schlachten gegen andere Spieler");
        this.winTarget = wins;
        setProgressionGoal(wins);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.isWinner(player) && !result.isNeutralLoser()) {
            wins++;
            check();
        }
    }

    @Override
    public void check() {
        updateProgression(wins);
        if (wins >= winTarget) {
            success();
        }
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setSpreadUnits(false);
        strategy.setPrepareStrategy(() -> {
            Set<Location> locations = game.getAllPlayers().stream().filter(it -> it != player)
                    .flatMap(it -> it.getUnits().stream()).map(BattleObject::getLocation).collect(Collectors.toSet());
            strategy.updateTargets(locations);
            strategy.setRequiredUnits((winTarget - wins) * 3);
        });
    }
}
