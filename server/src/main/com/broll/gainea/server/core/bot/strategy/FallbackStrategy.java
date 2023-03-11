package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class FallbackStrategy {

    public static GoalStrategy create(BotStrategy botStrategy, Player player, GameContainer game, StrategyConstants constants) {
        GoalStrategy strategy = new GoalStrategy(botStrategy, null, player, game, constants);
        strategy.setPrepareStrategy(() -> {
            //attack monsters
            Set<Location> locations = game.getObjects().stream().filter(it -> it instanceof Monster).map(MapObject::getLocation).collect(Collectors.toSet());
            strategy.updateTargets(locations);
            strategy.setRequiredUnits(0);
        });
        return strategy;
    }

}
