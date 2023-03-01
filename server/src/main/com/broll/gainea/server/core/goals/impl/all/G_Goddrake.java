package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.MapObject;
import com.google.common.collect.Sets;

import java.util.HashSet;

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

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setPrepareStrategy(()->{
            Location dragonLocation= game.getObjects().stream().filter(it-> it instanceof GodDragon).map(MapObject::getLocation).findFirst().orElse(null);
            if(dragonLocation==null){
                strategy.updateTargets(new HashSet<>());
            }
            else{
                strategy.updateTargets(Sets.newHashSet(dragonLocation));
                strategy.setRequiredUnits(5);
            }
        });
    }
}