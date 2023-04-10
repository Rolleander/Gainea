package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;

import java.util.Set;
import java.util.stream.Collectors;

public class G_KillStrongMonster extends Goal {

    public G_KillStrongMonster() {
        super(GoalDifficulty.MEDIUM, "Besiege ein Monster mit 4 oder mehr Sternen mit nur einer Einheit");
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
            return monster.getStars() >= 4 && unit.isDead();
        }
        return false;
    }

    @Override
    public void check() {

    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setSpreadUnits(false);
        strategy.setPrepareStrategy(() -> {
            Set<Location> locations = game.getObjects().stream().filter(it -> it instanceof Unit && isTarget((Unit) it)).map(MapObject::getLocation).collect(Collectors.toSet());
            strategy.updateTargets(locations);
            strategy.setRequiredUnits(1);
        });
    }
}
