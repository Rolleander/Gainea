package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.UnitControl;

public class MonkFraction extends Fraction {
    public MonkFraction() {
        super(FractionType.MONKS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Jede Runde erhält eine Einheit +1 Leben");
        desc.contra("Gegen Monster -1 Zahl");
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        if (context.getOpposingFightingArmy(soldier).stream().anyMatch(it -> it instanceof Monster)) {
            power.changeNumberPlus(-1);
        }
        return power;
    }

    @Override
    public void turnStarted(ActionHandlers actionHandlers) {
        if (!owner.getUnits().isEmpty()) {
            Unit randomUnit = owner.getUnits().get(RandomUtils.random(owner.getUnits().size() - 1));
            //inc life
            randomUnit.getMaxHealth().addValue(1);
            //heal
            UnitControl.heal(game, randomUnit, 1);
        }
        super.turnStarted(actionHandlers);
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Mönch");
        soldier.setIcon(108);
        return soldier;
    }


    @Override
    public Soldier createCommander() {
        Soldier commander = new Soldier(owner);
        commander.setCommander(true);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Großmeister Eron");
        commander.setIcon(107);
        return commander;
    }

}
