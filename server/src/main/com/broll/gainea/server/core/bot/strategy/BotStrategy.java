package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BotStrategy {

    private final static Logger Log = LoggerFactory.getLogger(BotStrategy.class);
    private StrategyConstants constants;
    private GameContainer game;
    private Player player;
    private List<GoalStrategy> goalStrategies = new ArrayList<>();
    private Map<Unit, GoalStrategy> strategizedUnits = new HashMap<>();
    private Map<Unit, Location> moveTargets = new HashMap<>();
    private GoalStrategy fallbackStrategy;

    public BotStrategy(GameContainer game, Player player, StrategyConstants constants) {
        this.game = game;
        this.player = player;
        this.constants = constants;
        this.fallbackStrategy = FallbackStrategy.create(this, player, game, constants);
    }

    public GoalStrategy getFallbackStrategy() {
        return fallbackStrategy;
    }

    public Map<Unit, Location> getMoveTargets() {
        return moveTargets;
    }

    public StrategyConstants getConstants() {
        return constants;
    }

    public GoalStrategy getStrategy(Unit unit) {
        return strategizedUnits.get(unit);
    }

    public List<GoalStrategy> getGoalStrategies() {
        return goalStrategies;
    }

    private void updateUnitStrategies() {
        for (Unit unit : player.getUnits()) {
            GoalStrategy strategy = strategizedUnits.get(unit);
            if (strategy == null || strategy.getTargetLocations().isEmpty()) {
                strategizeUnit(unit);
            }
        }
    }

    private void strategizeUnit(Unit unit) {
        List<GoalStrategy> targetGoals = goalStrategies.stream().filter(it -> !it.getTargetLocations().isEmpty()).collect(Collectors.toList());
        List<GoalStrategy> needyGoals = targetGoals.stream().filter(GoalStrategy::requiresMoreUnits).collect(Collectors.toList());
        if (!needyGoals.isEmpty()) {
            GoalStrategy goal = needyGoals.get(BotUtils.getLowestScoreIndex(needyGoals,
                    it -> it.getClosestDistance(unit, unit.getLocation())));
            strategizeUnit(goal, unit);
            return;
        }
        if (targetGoals.isEmpty()) {
            strategizeUnit(fallbackStrategy, unit);
        } else {
            strategizeUnit(RandomUtils.pickRandom(targetGoals), unit);
        }
    }

    private void strategizeUnit(GoalStrategy goal, Unit unit) {
        Log.trace("Strategize " + unit + " to goal " + goal);
        goal.strategizeUnit(unit);
        strategizedUnits.put(unit, goal);
    }

    public void prepareTurn() {
        updateUnitStrategies();
        goalStrategies.forEach(goal -> {
            List<Unit> deadUnits = goal.getUnits().stream().filter(Unit::isDead).collect(Collectors.toList());
            deadUnits.forEach(unit -> {
                strategizedUnits.remove(unit);
                moveTargets.remove(unit);
            });
            goal.getUnits().removeAll(deadUnits);
            goal.prepare();
        });
    }

    public void synchronizeGoalStrategies() {
        List<Goal> goals = player.getGoalHandler().getGoals();
        goals.forEach(goal -> {
            if (goalStrategies.stream().noneMatch(it -> it.getGoal() == goal)) {
                goalStrategies.add(new GoalStrategy(this, goal, player, game, constants));
            }
        });
        for (GoalStrategy strategy : goalStrategies) {
            if (!goals.contains(strategy.getGoal())) {
                clearGoal(strategy);
            }
        }
    }

    public void restrategizeUnits(GoalStrategy goal) {
        goal.getUnits().forEach(it -> {
            strategizedUnits.remove(it);
            moveTargets.remove(it);
        });
        goal.getUnits().clear();
    }

    private void clearGoal(GoalStrategy goal) {
        goalStrategies.remove(goal);
        restrategizeUnits(goal);
    }

    public int chooseUnitPlace(Unit unit, List<Location> locations) {
        if (unit.getOwner() != player) {
            return RandomUtils.random(0, locations.size() - 1);
        }
        List<GoalStrategy> goals = goalStrategies.stream().filter(GoalStrategy::requiresMoreUnits).collect(Collectors.toList());
        if (goals.isEmpty()) {
            return RandomUtils.random(0, locations.size() - 1);
        }
        Map<Location, MutablePair<GoalStrategy, Integer>> scores = new HashMap<>();
        locations.forEach(it -> scores.put(it, MutablePair.of(null, Integer.MAX_VALUE)));
        goals.forEach(it -> it.scoreLocations(unit, scores));
        Map.Entry<Location, MutablePair<GoalStrategy, Integer>> entry = BotUtils.getLowestScoreEntry(new ArrayList<>(scores.entrySet()), it -> it.getValue().getRight());
        int option = locations.indexOf(entry.getKey());
        GoalStrategy strategy = entry.getValue().getKey();
        if (strategy == null) {
            strategy = getFallbackStrategy();
        }
        strategizeUnit(strategy, unit);
        return option;
    }

}
