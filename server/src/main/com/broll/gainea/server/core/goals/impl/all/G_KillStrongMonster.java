package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;

public class G_KillStrongMonster extends Goal {

    public G_KillStrongMonster() {
        super(GoalDifficulty.MEDIUM, "Töte ein Monster mit 4 oder mehr Sternen mit nur einer Einheit");
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if (unit instanceof Monster) {
            Monster monster = (Monster) unit;
            if (monster.getOwner() == null && monster.getStars() >= 4 && throughBattle != null
                    && throughBattle.getAttackingPlayer() == player && throughBattle.getAttackers().size() == 1) {
                success();
            }
        }
    }


    @Override
    public void check() {

    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        //todo
    }
}
