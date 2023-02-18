package com.broll.gainea.server.core.goals.impl.e2;

import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.stream.Collectors;

public class G_Snow extends CustomOccupyGoal {

    private final static int COUNT = 5;

    public G_Snow() {
        super(GoalDifficulty.EASY, "Erobere " + COUNT + " Schneegebiete");
        setExpansionRestriction(ExpansionType.ICELANDS);
        setProgressionGoal(COUNT);
    }

    @Override
    public void check() {
        int count = (int) LocationUtils.getControlledLocationsIn(player, ExpansionType.ICELANDS).stream().filter(it -> LocationUtils.isAreaType(it, AreaType.SNOW)).count();
        updateProgression(count);
        if (count >= COUNT) {
            success();
        }
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setRequiredUnits(COUNT);
        strategy.updateTargets(game.getMap().getAllAreas().stream().filter(it -> it.getType() == AreaType.SNOW).collect(Collectors.toSet()));
    }
}
