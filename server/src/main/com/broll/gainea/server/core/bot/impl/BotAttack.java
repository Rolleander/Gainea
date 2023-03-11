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
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.LocationUtils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BotAttack extends BotOptionalAction<NT_Action_Attack, BotAttack.AttackOption> {

    private final static int FIGHT_TARGET = 0;
    private final static int FIGHT_PLAYER = 1;
    private final static int FIGHT_WILD = 2;

    @Override
    public AttackOption score(NT_Action_Attack action) {
        Location location = BotUtils.getLocation(game, action.location);
        List<Unit> usableUnits = usableUnits(action.units);
        int fightType = getFighType(location);
        float winChance = getRequiredWinChance(fightType);
        int[] unitIds = Arrays.stream(action.units).mapToInt(it -> it.id).toArray();
        List<Location> attackFromOptions = usableUnits.stream().map(MapObject::getLocation).distinct().collect(Collectors.toList());
        for (Location attackFrom : attackFromOptions) {
            List<Unit> groupedUnits = usableUnits.stream().filter(it -> it.getLocation() == attackFrom).collect(Collectors.toList());
            List<Unit> fighters = BattleSimulation.calculateRequiredFighters(location, groupedUnits, winChance);
            if (fighters != null) {
                AttackOption option = new AttackOption((3 - fightType) * 5);
                option.type = fightType;
                option.location = location;
                option.attackUnits = fighters.stream().mapToInt(it -> ArrayUtils.indexOf(unitIds, it.getId())).toArray();
                return option;
            }
        }
        return null;
    }


    @Override
    protected void react(NT_Action_Attack action, NT_Reaction reaction) {
        reaction.options = getSelectedOption().attackUnits;
    }

    @Override
    public Class<NT_Action_Attack> getActionClass() {
        return NT_Action_Attack.class;
    }


    public boolean keepAttacking(NT_Battle_Update update) {
        List<Unit> attackers = BotUtils.getObjects(game, update.attackers);
        List<Unit> defenders = BotUtils.getObjects(game, update.defenders);
        float winChance = getRequiredWinChance(getSelectedOption().type);
        return BattleSimulation.calculateCurrentWinChance(attackers, defenders) >= winChance;
    }

    private List<Unit> usableUnits(NT_Unit[] ntUnits) {
        List<Unit> units = BotUtils.getObjects(game, ntUnits);
        return units.stream().filter(it -> {
            if (strategy.getMoveTargets().get(it) == it.getLocation()) {
                return false;
            }
            GoalStrategy goalStrategy = strategy.getStrategy(it);
            if (goalStrategy == null) {
                return true;
            }
            return goalStrategy.allowFighting(it);
        }).collect(Collectors.toList());
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


    private int getFighType(Location location) {
        if (isTargetLocation(location)) {
            return FIGHT_TARGET;
        }
        if (!LocationUtils.getMonsters(location).isEmpty()) {
            return FIGHT_WILD;
        }
        return FIGHT_PLAYER;
    }

    private boolean isTargetLocation(Location location) {
        return strategy.getGoalStrategies().stream().flatMap(it -> it.getTargetLocations().stream()).anyMatch(it -> it == location);
    }

    public static class AttackOption extends BotOption {
        private int[] attackUnits;
        private int type;
        private Location location;

        public AttackOption(float score) {
            super(score);
        }
    }

}
