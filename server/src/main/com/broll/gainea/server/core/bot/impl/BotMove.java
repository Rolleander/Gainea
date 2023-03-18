package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.bot.BotOptionalAction;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.LocationUtils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BotMove extends BotOptionalAction<NT_Action_Move, BotMove.MoveOption> {

    private final static int MOVE_SCORE = 10;


    @Override
    protected void react(NT_Action_Move action, NT_Reaction reaction) {
        reaction.options = getSelectedOption().moveUnits;
    }

    @Override
    public MoveOption score(NT_Action_Move action) {
        Location location = BotUtils.getLocation(game, action.location);
        List<Unit> units = BotUtils.getObjects(game, action.units);
        List<GoalStrategy> goalStrategies = units.stream().map(it -> strategy.getStrategy(it)).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        for (GoalStrategy goalStrategy : goalStrategies) {
            List<Unit> goalUnits = units.stream().filter(it -> strategy.getStrategy(it) == goalStrategy).collect(Collectors.toList());
            MoveOption move = chooseUnits(goalStrategy, action.units, goalUnits, location);
            if (move != null) {
                return move;
            }
        }
        return null;
    }


    @Override
    public Class<NT_Action_Move> getActionClass() {
        return NT_Action_Move.class;
    }


    private MoveOption chooseUnits(GoalStrategy goalStrategy, NT_Unit[] nt_units, List<Unit> units, Location to) {
        List<Location> unitTargets = getPathTargets(units, goalStrategy);
        int distance = Integer.MAX_VALUE;
        List<Unit> moveTogether = new ArrayList<>();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            Location unitTarget = unitTargets.get(i);
            if (unitTarget == null || unitTarget == unit.getLocation()) {
                continue;
            }
            int currentTargetDistance = LocationUtils.getWalkingDistance(unit, unit.getLocation(), unitTarget);
            int moveTargetDistance = LocationUtils.getWalkingDistance(unit, to, unitTarget);
            if (moveTargetDistance < currentTargetDistance) {
                distance = Math.min(moveTargetDistance, distance);
                moveTogether.add(unit);
            }
        }
        if (moveTogether.isEmpty()) {
            return null;
        }
        int[] unitIds = Arrays.stream(nt_units).mapToInt(it -> it.id).toArray();
        MoveOption moveOption = new MoveOption(Math.max(MOVE_SCORE - distance, 1));
        moveOption.location = to;
        moveOption.moveUnits = moveTogether.stream().mapToInt(it -> ArrayUtils.indexOf(unitIds, it.getId())).toArray();
        return moveOption;
    }

    private List<Location> getPathTargets(List<Unit> units, GoalStrategy goalStrategy) {
        return units.stream().map(it -> {
            Location target = strategy.getMoveTargets().get(it);
            if (target == null) {
                return createPath(it, goalStrategy);
            }
            return target;
        }).collect(Collectors.toList());
    }

    private Location createPath(Unit unit, GoalStrategy goalStrategy) {
        Location target = provideNextTarget(unit, goalStrategy);
        if (target != null) {
            strategy.getMoveTargets().put(unit, target);
        }
        return target;
    }

    private Location provideNextTarget(Unit unit, GoalStrategy goalStrategy) {
        Set<Location> targets = goalStrategy.getTargetLocations();
        if (targets.size() == 1) {
            return targets.iterator().next();
        }
        if (targets.isEmpty()) {
            return null;
        }
        Map<Location, AtomicInteger> targetCounts = new HashMap<>();
        targets.forEach(it -> {
            int distance = LocationUtils.getWalkingDistance(unit, unit.getLocation(), it);
            if (distance == -1) {
                distance = 100;
            }
            targetCounts.put(it, new AtomicInteger(distance)
            );
        });
        if (goalStrategy.isSpreadUnits()) {
            goalStrategy.getUnits().stream().map(it -> strategy.getMoveTargets().get(it)).forEach(target -> {
                AtomicInteger count = targetCounts.get(target);
                if (count != null) {
                    count.addAndGet(1000);
                }
            });
        }
        //todo check if target is same as existing unit but closer,
        return BotUtils.getLowestScoreEntry(new ArrayList<>(targetCounts.entrySet()),
                it -> it.getValue().get()).getKey();
    }

    public static class MoveOption extends BotOption {
        private int[] moveUnits;

        private Location location;

        public MoveOption(float score) {
            super(score);
        }

        @Override
        public String toString() {
            return "move to " + location + " with " + moveUnits.length + " units";
        }
    }

}
