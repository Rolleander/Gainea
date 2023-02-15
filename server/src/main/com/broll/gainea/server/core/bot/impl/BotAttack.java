package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.bot.BotAction;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.bot.strategy.BattleSimulation;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.LocationUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BotAttack extends BotAction<NT_Action_Attack> {

    private final static int FIGHT_TARGET = 0;
    private final static int FIGHT_PLAYER = 1;
    private final static int FIGHT_WILD = 2;

    private int[] attackUnits;
    private int moveTo;

    @Override
    protected void handleAction(NT_Action_Attack action, NT_Reaction reaction) {
        reaction.option = moveTo;
        reaction.options = attackUnits;
    }

    @Override
    public Class<NT_Action_Attack> getActionClass() {
        return NT_Action_Attack.class;
    }

    @Override
    public float score(NT_Action_Attack action, NT_PlayerTurnActions turn) {
        List<Location> locations = BotUtils.getLocations(game, action.attackLocations);
        List<BattleObject> usableUnits = usableUnits(action.units);
        List<Pair<Location, Integer>> options = orderByPriority(locations);
        int[] unitIds = Arrays.stream(action.units).mapToInt(it -> it.id).toArray();
        Optional<Location> singleTarget = hasExactlyOneTargetFight(options);
        if (singleTarget.isPresent()) {
            moveTo = locations.indexOf(singleTarget.get());
            attackUnits = usableUnits.stream().mapToInt(it -> ArrayUtils.indexOf(unitIds, it.getId())).toArray();
            return 15;
        }
        for (Pair<Location, Integer> option : options) {
            float winChance = getRequiredWinChance(option.getValue());
            List<BattleObject> fighters = BattleSimulation.caclulateRequiredFighters(option.getKey(), usableUnits, winChance);
            if (fighters != null) {
                moveTo = locations.indexOf(option.getKey());
                attackUnits = fighters.stream().mapToInt(it -> ArrayUtils.indexOf(unitIds, it.getId())).toArray();
                return (3 - option.getValue()) * 5;
            }
        }
        return -1;
    }

    private List<BattleObject> usableUnits(NT_Unit[] ntUnits) {
        List<BattleObject> units = Arrays.stream(ntUnits).map(it -> BotUtils.getObject(game, it)).collect(Collectors.toList());
        return units.stream().filter(it -> strategy.getStrategy(it).allowFighting(it)).collect(Collectors.toList());
    }

    private Optional<Location> hasExactlyOneTargetFight(List<Pair<Location, Integer>> options) {
        List<Pair<Location, Integer>> targetFights = options.stream().filter(it -> it.getValue() == FIGHT_TARGET).collect(Collectors.toList());
        if (targetFights.size() == 1) {
            return Optional.of(targetFights.get(0).getKey());
        }
        return Optional.empty();
    }

    private float getRequiredWinChance(int fightType) {
        switch (fightType) {
            case FIGHT_TARGET:
                return strategy.getConstants().getWinchanceForTargetConquer();
            case FIGHT_PLAYER:
                return strategy.getConstants().getWinchanceForPlayerFight();
            case FIGHT_WILD:
                return strategy.getConstants().getWinchanceForWildFight();
        }
        return 0;
    }

    private List<Pair<Location, Integer>> orderByPriority(List<Location> locations) {
        return locations.stream().map(this::prioritize).sorted(Comparator.comparingInt(Pair::getValue)).collect(Collectors.toList());
    }

    private Pair<Location, Integer> prioritize(Location location) {
        if (isTargetLocation(location)) {
            return Pair.of(location, FIGHT_TARGET);
        }
        if (!LocationUtils.getMonsters(location).isEmpty()) {
            return Pair.of(location, FIGHT_WILD);
        }
        return Pair.of(location, FIGHT_PLAYER);
    }

    private boolean isTargetLocation(Location location) {
        return strategy.getGoalStrategies().stream().flatMap(it -> it.getTargetLocations().stream()).anyMatch(it -> it == location);
    }
}
