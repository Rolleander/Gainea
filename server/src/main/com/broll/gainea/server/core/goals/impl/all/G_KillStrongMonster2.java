package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;

public class G_KillStrongMonster2 extends Goal {

    private final static int KILLS = 4;

    public G_KillStrongMonster2() {
        super(GoalDifficulty.EASY, "Besiege ein Monster mit " + KILLS + " oder mehr Kills.");
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.getOpposingUnits(player).stream().anyMatch(this::isTarget)) {
            success();
        }
    }

    private boolean isTarget(Unit unit) {
        if (unit instanceof Monster) {
            Monster monster = (Monster) unit;
            return monster.getKills() >= KILLS && unit.isDead();
        }
        return false;
    }

    @Override
    public void check() {

    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        //todo
    }
}