package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.bot.BotOptionalAction;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.LocationUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BotMove extends BotOptionalAction<NT_Action_Move, BotMove.MoveOption> {

    private final static int MOVE_SCORE = 10;


    @Override
    protected void react(NT_Action_Move action, NT_Reaction reaction) {
        reaction.option = getSelectedOption().moveTo;
        reaction.options = getSelectedOption().moveUnits;
    }

    @Override
    public MoveOption score(NT_Action_Move action) {
        List<Location> locations = BotUtils.getLocations(game, action.possibleLocations);
        List<BattleObject> units = BotUtils.getObjects(game, action.units);
        List<GoalStrategy> goalStrategies = units.stream().map(it -> strategy.getStrategy(it)).distinct().collect(Collectors.toList());
        for (GoalStrategy goalStrategy : goalStrategies) {
            List<BattleObject> goalUnits = units.stream().filter(it -> strategy.getStrategy(it) == goalStrategy).collect(Collectors.toList());
            MoveOption move = chooseStep(goalStrategy, action.units, goalUnits, locations);
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


    private MoveOption chooseStep(GoalStrategy goalStrategy, NT_Unit[] nt_units, List<BattleObject> units, List<Location> options) {
        List<Location> targets = getPathTargets(units, goalStrategy);
        Location option = null;
        int distance = Integer.MAX_VALUE;
        List<BattleObject> moveTogether = new ArrayList<>();
        for (int i = 0; i < units.size(); i++) {
            BattleObject unit = units.get(i);
            Location target = targets.get(i);
            if (target == null || target == unit.getLocation()) {
                continue;
            }
            Pair<Location, Integer> path = BotUtils.getBestPath(bot, options, target);
            if (option == null) {
                option = path.getKey();
                distance = path.getValue();
                moveTogether.add(unit);
            } else if (option == path.getKey()) {
                distance = Math.min(distance, path.getValue());
                moveTogether.add(unit);
            }
        }
        if (option == null) {
            return null;
        }
        int[] unitIds = Arrays.stream(nt_units).mapToInt(it -> it.id).toArray();
        MoveOption moveOption = new MoveOption(Math.max(MOVE_SCORE - distance, 1));
        moveOption.moveTo = options.indexOf(option);
        moveOption.moveUnits = moveTogether.stream().mapToInt(it -> ArrayUtils.indexOf(unitIds, it.getId())).toArray();
        return moveOption;
    }

    private List<Location> getPathTargets(List<BattleObject> units, GoalStrategy goalStrategy) {
        return units.stream().map(it -> {
            Location target = strategy.getMoveTargets().get(it);
            if (target == null) {
                return createPath(it, goalStrategy);
            }
            return target;
        }).collect(Collectors.toList());
    }

    private Location createPath(BattleObject unit, GoalStrategy goalStrategy) {
        Location target = provideNextTarget(unit.getLocation(), goalStrategy);
        if (target != null) {
            strategy.getMoveTargets().put(unit, target);
        }
        return target;
    }

    private Location provideNextTarget(Location from, GoalStrategy goalStrategy) {
        Set<Location> targets = goalStrategy.getTargetLocations();
        if (targets.size() == 1) {
            return targets.iterator().next();
        }
        if (targets.isEmpty()) {
            return null;
        }
        Map<Location, AtomicInteger> targetCounts = new HashMap<>();
        targets.forEach(it -> {
            int distance = LocationUtils.getWalkingDistance(bot, from, it);
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
        private int moveTo;

        public MoveOption(float score) {
            super(score);
        }
    }

}
