package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class G_KillMonsters extends Goal {
    private final int starsTarget;
    private int stars;

    public G_KillMonsters() {
        this(GoalDifficulty.EASY, 7);
    }

    public G_KillMonsters(GoalDifficulty difficulty, int stars) {
        super(difficulty, "Erledige Monster mit insgesamt " + stars + " Sternen");
        setProgressionGoal(stars);
        this.starsTarget = stars;
    }

    @Override
    public void earnedStars(Player player, int stars) {
        if (player == this.player) {
            this.stars += stars;
            check();
        }
    }

    @Override
    public void check() {
        updateProgression(this.stars);
        if (this.stars >= starsTarget) {
            success();
        }
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setSpreadUnits(false);
        strategy.setPrepareStrategy(() -> {
            Set<Location> locations = game.getObjects().stream().filter(it -> it instanceof Monster).map(MapObject::getLocation).collect(Collectors.toSet());
            strategy.updateTargets(locations);
            strategy.setRequiredUnits(starsTarget - stars);
        });
    }
}
