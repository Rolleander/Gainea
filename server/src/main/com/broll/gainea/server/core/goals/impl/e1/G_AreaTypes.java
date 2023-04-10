package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;

import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class G_AreaTypes extends CustomOccupyGoal {

    private final static AreaType[] TYPES = new AreaType[]{AreaType.PLAINS, AreaType.DESERT, AreaType.LAKE, AreaType.MOUNTAIN};

    public G_AreaTypes() {
        super(GoalDifficulty.MEDIUM, "Erobere zwei beliebige Steppen, Wüsten, Meere und Berge");
        setExpansionRestriction(ExpansionType.GAINEA);
        setProgressionGoal(8);
    }

    @Override
    public void check() {
        int count = 0;
        List<Location> locations = LocationUtils.getControlledLocationsIn(player, ExpansionType.GAINEA);
        for (AreaType type : TYPES) {
            count += Math.min(2, LocationUtils.filterByType(locations, type).count());
        }
        updateProgression(count);
        if (count == TYPES.length * 2) {
            success();
        }
    }

    private Stream<Location> getUnoccupiedOfType(AreaType type) {
        List<Location> areas = game.getMap().getExpansion(ExpansionType.GAINEA).getAllAreas().stream()
                .filter(it -> it.getType() == type).collect(Collectors.toList());
        List<Location> controlled = areas.stream().filter(it -> it.getInhabitants().stream().anyMatch(unit -> unit.getOwner() == player)).collect(Collectors.toList());
        if (controlled.size() >= 2) {
            return Stream.of();
        }
        return ListUtils.subtract(areas, controlled).stream();
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setPrepareStrategy(() -> {
            Set<Location> targets = Arrays.stream(TYPES).flatMap(this::getUnoccupiedOfType).collect(Collectors.toSet());
            strategy.updateTargets(targets);
        });
    }
}
