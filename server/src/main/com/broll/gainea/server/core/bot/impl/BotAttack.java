package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.bot.BotOptionalAction;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.bot.strategy.BattleSimulation;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
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

public class BotAttack extends BotOptionalAction<NT_Action_Attack, BotAttack.AttackOption> {

    private final static int FIGHT_TARGET = 0;
    private final static int FIGHT_PLAYER = 1;
    private final static int FIGHT_WILD = 2;


    @Override
    public AttackOption score(NT_Action_Attack action) {
        List<Location> locations = BotUtils.getLocations(game, action.attackLocations);
        List<BattleObject> usableUnits = usableUnits(action.units);
        List<Pair<Location, Integer>> options = orderByPriority(locations);
        int[] unitIds = Arrays.stream(action.units).mapToInt(it -> it.id).toArray();
        Optional<AttackOption> singleTarget = hasSingleTargetFight(options, usableUnits, locations, unitIds);
        if (singleTarget.isPresent()) {
            return singleTarget.get();
        }
        for (Pair<Location, Integer> attackOption : options) {
            float winChance = getRequiredWinChance(attackOption.getValue());
            List<BattleObject> fighters = BattleSimulation.calculateRequiredFighters(attackOption.getKey(), usableUnits, winChance);
            if (fighters != null) {
                AttackOption option = new AttackOption((3 - attackOption.getValue()) * 5);
                option.type = attackOption.getValue();
                option.location = attackOption.getKey();
                option.attackTo = locations.indexOf(attackOption.getKey());
                option.attackUnits = fighters.stream().mapToInt(it -> ArrayUtils.indexOf(unitIds, it.getId())).toArray();
                return option;
            }
        }
        return null;
    }

    private Optional<AttackOption> hasSingleTargetFight(List<Pair<Location, Integer>> options, List<BattleObject> usableUnits, List<Location> locations, int[] unitIds) {
        Optional<Location> singleTarget = hasExactlyOneTargetFight(options);
        if (singleTarget.isPresent() && !usableUnits.isEmpty() && BattleSimulation.calculateWinChance(singleTarget.get(), usableUnits) >= strategy.getConstants().getWinchanceForTargetConquer()) {
            AttackOption option = new AttackOption(15);
            option.type = FIGHT_TARGET;
            option.location = singleTarget.get();
            option.attackTo = locations.indexOf(singleTarget.get());
            option.attackUnits = usableUnits.stream().mapToInt(it -> ArrayUtils.indexOf(unitIds, it.getId())).toArray();
            return Optional.of(option);
        }
        return Optional.empty();
    }


    @Override
    protected void react(NT_Action_Attack action, NT_Reaction reaction) {
        reaction.option = getSelectedOption().attackTo;
        reaction.options = getSelectedOption().attackUnits;
    }

    @Override
    public Class<NT_Action_Attack> getActionClass() {
        return NT_Action_Attack.class;
    }


    public boolean keepAttacking(NT_Battle_Update update) {
        List<BattleObject> attackers = BotUtils.getObjects(game, update.attackers);
        List<BattleObject> defenders = BotUtils.getObjects(game, update.defenders);
        float winChance = getRequiredWinChance(getSelectedOption().type);
        return BattleSimulation.calculateAttackerWinChance(getSelectedOption().location, attackers, defenders) >= winChance;
    }

    private List<BattleObject> usableUnits(NT_Unit[] ntUnits) {
        List<BattleObject> units = BotUtils.getObjects(game, ntUnits);
        return units.stream().filter(it -> {
            GoalStrategy goalStrategy = strategy.getStrategy(it);
            if (goalStrategy == null) {
                return true;
            }
            return goalStrategy.allowFighting(it);
        }).collect(Collectors.toList());
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

    public static class AttackOption extends BotOption {
        private int[] attackUnits;
        private int attackTo;
        private int type;
        private Location location;

        public AttackOption(float score) {
            super(score);
        }
    }

}
