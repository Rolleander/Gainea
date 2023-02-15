package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BotStrategy {

    private StrategyConstants constants = new StrategyConstants();
    private GameContainer game;
    private Player player;
    private List<GoalStrategy> goalStrategies = new ArrayList<>();
    private Map<BattleObject, GoalStrategy> strategizedUnits = new HashMap<>();
    private Map<BattleObject, Location> moveTargets = new HashMap<>();

    public BotStrategy(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
    }

    public Map<BattleObject, Location> getMoveTargets() {
        return moveTargets;
    }

    public StrategyConstants getConstants() {
        return constants;
    }

    public GoalStrategy getStrategy(BattleObject unit) {
        return strategizedUnits.get(unit);
    }

    public List<GoalStrategy> getGoalStrategies() {
        return goalStrategies;
    }

    private void strategizeNewUnits() {
        for (BattleObject unit : player.getUnits()) {
            if (!strategizedUnits.containsKey(unit)) {
                strategizeUnit(unit);
            }
        }
    }

    private void strategizeUnit(BattleObject unit) {
        List<GoalStrategy> goals = goalStrategies.stream().filter(GoalStrategy::requiresMoreUnits).collect(Collectors.toList());
        if (!goals.isEmpty()) {
            GoalStrategy goal = goals.get(BotUtils.getLowestScoreIndex(goals,
                    it -> it.getClosestDistance(unit.getLocation())));
            strategizeUnit(goal, unit);
            return;
        }
        strategizeUnit(RandomUtils.pickRandom(goalStrategies), unit);
    }

    private void strategizeUnit(GoalStrategy goal, BattleObject unit) {
        goal.strategizeUnit(unit);
        strategizedUnits.put(unit, goal);
    }

    public void prepareTurn() {
        strategizeNewUnits();
        goalStrategies.forEach(GoalStrategy::prepare);
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
        goal.getUnits().forEach(it -> strategizedUnits.remove(it));
        goal.getUnits().clear();
    }

    private void clearGoal(GoalStrategy goal) {
        goalStrategies.remove(goal);
        restrategizeUnits(goal);
    }

    public int chooseUnitPlace(BattleObject unit, List<Location> locations) {
        if (unit.getOwner() != player) {
            return RandomUtils.random(0, locations.size() - 1);
        }
        List<GoalStrategy> goals = goalStrategies.stream().filter(GoalStrategy::requiresMoreUnits).collect(Collectors.toList());
        if (goals.isEmpty()) {
            return RandomUtils.random(0, locations.size() - 1);
        }
        Map<Location, MutablePair<GoalStrategy, Integer>> scores = new HashMap<>();
        locations.forEach(it -> scores.put(it, MutablePair.of(null, Integer.MAX_VALUE)));
        goals.forEach(it -> it.scoreLocations(scores));
        Map.Entry<Location, MutablePair<GoalStrategy, Integer>> entry = BotUtils.getLowestScoreEntry(new ArrayList<>(scores.entrySet()), it -> it.getValue().getRight());
        int option = locations.indexOf(entry.getKey());
        GoalStrategy strategy = entry.getValue().getKey();
        strategizeUnit(strategy, unit);
        return option;
    }
}
