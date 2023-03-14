package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class G_MoveUnit extends CustomOccupyGoal {

    private Area from, to;
    private int distance;
    private List<Unit> walkingUnits = new ArrayList<>();

    public G_MoveUnit() {
        this(GoalDifficulty.EASY, 6);
    }

    public G_MoveUnit(GoalDifficulty difficulty, int distance) {
        super(difficulty, "");
        this.distance = distance;
    }

    private void findPath() {
        from = (Area) LocationUtils.getRandomFree(game.getMap().getAllAreas());
        int step = 0;
        List<Location> visited = new ArrayList<>();
        List<Location> remaining = from.getWalkableNeighbours();
        do {
            step++;
            if (step == distance) {
                Collections.shuffle(remaining);
                to = (Area) remaining.stream().filter(it -> it instanceof Area).findFirst().orElse(null);
                if (to != null) {
                    return;
                }
            }
            visited.addAll(remaining);
            remaining = remaining.stream().flatMap(it -> it.getWalkableNeighbours().stream()).collect(Collectors.toList());
            remaining.removeAll(visited);
        } while (!remaining.isEmpty());
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
        List<Area> startingPoints = game.getMap().getAllAreas();
        Collections.shuffle(startingPoints);
        while (!startingPoints.isEmpty() && to == null) {
            from = startingPoints.remove(0);
            findPath();
        }
        if (to == null) {
            return false;
        }
        this.locations.add(from);
        this.locations.add(to);
        this.text = "Bewege eine Einheit von " + from.getName() + " nach " + to.getName();
        setProgressionGoal(distance);
        return super.init(game, player);
    }

    @Override
    public void check() {
        walkingUnits.removeIf(Unit::isDead);
        walkingUnits.addAll(PlayerUtils.getUnits(player, from));
        if (walkingUnits.stream().anyMatch(it -> it.getLocation() == to)) {
            updateProgression(distance);
            success();
        } else {
            //todo: stimmt noch nicht bei mehreren einheiten?
            int closestDistance = walkingUnits.stream().map(it -> LocationUtils.getWalkingDistance(it, it.getLocation(), to)).reduce(distance, Math::min);
            updateProgression(distance - closestDistance);
        }
    }


    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setRequiredUnits(1);
        strategy.setSpreadUnits(false);
        strategy.updateTargets(Sets.newHashSet(from));
        strategy.setPrepareStrategy(() -> {
            strategy.getUnits().stream().filter(it -> walkingUnits.contains(it)).forEach(unit -> {
                strategy.getBotStrategy().getMoveTargets().put(unit, to);
            });
        });
    }
}
