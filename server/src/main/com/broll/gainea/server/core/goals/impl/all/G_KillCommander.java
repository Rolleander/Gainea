package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class G_KillCommander extends Goal {

    public G_KillCommander() {
        super(GoalDifficulty.EASY, "Besiege den Feldherr eines anderen Spielers im Kampf");
    }

    @Override
    public void killed(Unit unit, BattleResult throughBattle) {
        if (throughBattle != null) {
            if (throughBattle.isAttacker(player) && throughBattle.getDefenders().stream().anyMatch(it -> PlayerUtils.isCommander(it) && it.isDead())) {
                success();
            } else if (throughBattle.isDefender(player) && throughBattle.getAttackers().stream().anyMatch(it -> PlayerUtils.isCommander(it) && it.isDead())) {
                success();
            }
        }
    }

    @Override
    public void check() {

    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setSpreadUnits(false);
        strategy.setPrepareStrategy(() -> {
            Set<Location> locations = game.getAllPlayers().stream().filter(it -> it != player).flatMap(it -> it.getUnits().stream())
                    .filter(PlayerUtils::isCommander).map(MapObject::getLocation).collect(Collectors.toSet());
            strategy.updateTargets(locations);
            strategy.setRequiredUnits(5);
        });
    }
}
