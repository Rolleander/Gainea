package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GoalStrategy {

    public final static int IGNORE_OPTIONS = -1;
    private Goal goal;
    private Player player;
    private GameContainer game;
    private StrategyConstants constants;
    private List<BattleObject> units = new ArrayList<>();
    private Set<Location> targetLocations = new HashSet<>();
    private IPrepareStrategy prepareStrategy;
    private int requiredUnits;
    private float score = -1;
    private BotStrategy strategy;

    private boolean spreadUnits = true;

    public GoalStrategy(BotStrategy strategy, Goal goal, Player player, GameContainer game, StrategyConstants constants) {
        this.strategy = strategy;
        this.goal = goal;
        this.player = player;
        this.game = game;
        this.constants = constants;
        if (goal != null) {
            goal.botStrategy(this);
        }
    }

    public void setSpreadUnits(boolean spreadUnits) {
        this.spreadUnits = spreadUnits;
    }

    public boolean isSpreadUnits() {
        return spreadUnits;
    }

    public boolean allowFighting(BattleObject unit) {
        Location location = unit.getLocation();
        if (!targetLocations.contains(location)) {
            return true;
        }
        int lowestOccupation = getLowesOccupations();
        List<BattleObject> occupiers = getOccupyingUnits(location);
        int occupierCount = occupiers.size();
        int expendable = occupierCount - Math.max(1, lowestOccupation);
        int index = occupiers.indexOf(unit);
        return index < expendable && expendable > 0;
    }

    private List<BattleObject> getOccupyingUnits(Location location) {
        return units.stream().filter(it -> it.getLocation() == location).collect(Collectors.toList());
    }

    public List<BattleObject> getUnits() {
        return units;
    }

    public void updateTargets(Set<Location> targetLocations) {
        if (this.targetLocations.isEmpty() && !targetLocations.isEmpty()) {
            strategy.restrategizeUnits(strategy.getFallbackStrategy());
        }
        this.targetLocations = targetLocations;
        units.forEach(it -> strategy.getMoveTargets().remove(it));
        requiredUnits = Math.max(requiredUnits, targetLocations.size());
        if (targetLocations.isEmpty()) {
            strategy.restrategizeUnits(this);
        }
    }

    public void setPrepareStrategy(IPrepareStrategy prepareStrategy) {
        this.prepareStrategy = prepareStrategy;
    }

    public void setRequiredUnits(int requiredUnits) {
        this.requiredUnits = requiredUnits;
    }

    public void prepare() {
        if (prepareStrategy != null) {
            prepareStrategy.prepare();
        }
    }

    public boolean requiresMoreUnits() {
        return units.size() < requiredUnits && !getTargetLocations().isEmpty();
    }

    public void strategizeUnit(BattleObject unit) {
        this.units.add(unit);
    }

    public int getClosestDistance(Location location) {
        return BotUtils.getBestPath(player, location, getTargetLocations()).getValue();
    }

    public void scoreLocations(Map<Location, MutablePair<GoalStrategy, Integer>> locationScores) {
        for (Location location : locationScores.keySet()) {
            MutablePair<GoalStrategy, Integer> entry = locationScores.get(location);
            int score = getClosestDistance(location);
            if (score < entry.getValue()) {
                entry.setLeft(this);
                entry.setRight(score);
            }
        }
    }

    public int getLowesOccupations() {
        return targetLocations.stream().mapToInt(it -> PlayerUtils.getUnits(player, it).size()).min().orElse(0);
    }

    public Set<Location> getTargetLocations() {
        return targetLocations;
    }

    public Goal getGoal() {
        return goal;
    }

    public BotStrategy getBotStrategy() {
        return strategy;
    }
}
