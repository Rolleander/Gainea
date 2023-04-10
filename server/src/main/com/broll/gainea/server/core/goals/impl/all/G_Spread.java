package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;

import java.util.Set;
import java.util.stream.Collectors;

public class G_Spread extends CustomOccupyGoal {

    private final static int COUNT = 6;

    public G_Spread() {
        super(GoalDifficulty.MEDIUM, "Kontrolliere Einheiten auf " + COUNT + " verschiedenen Landmassen");
        setProgressionGoal(COUNT);
    }

    @Override
    public void check() {
        int containers = (int) player.getControlledLocations().stream().filter(it -> !(it instanceof Ship)).map(Location::getContainer).distinct().count();
        updateProgression(containers);
        if (containers >= COUNT) {
            success();
        }
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        Set<Location> areas = game.getMap().getAllContainers().stream().map(it -> RandomUtils.pickRandom(it.getAreas())).collect(Collectors.toSet());
        strategy.setRequiredUnits(COUNT);
        strategy.updateTargets(areas);
    }
}
