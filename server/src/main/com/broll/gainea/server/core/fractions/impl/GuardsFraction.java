package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;

public class GuardsFraction extends Fraction {

    public GuardsFraction() {
        super(FractionType.GUARDS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Als Verteidiger ist die niedrigste Würfelzahl 3");
        desc.contra("Als Angreifer Zahl -1");
        //todo buff
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        if (context.isAttacking(soldier)) {
            power.changeNumberPlus(-1);
        } else {
            power.withLowestNumber(3);
        }
        return power;
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Gardistenwache");
        soldier.setIcon(19);
        return soldier;
    }


    @Override
    public Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Elitegardist");
        commander.setIcon(15);
        return commander;
    }


}
