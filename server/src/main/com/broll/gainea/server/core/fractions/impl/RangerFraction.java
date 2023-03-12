package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;

public class RangerFraction extends Fraction {

    public RangerFraction() {
        super(FractionType.RANGER);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Gegen Monster +1 Zahl");
        desc.contra("Auf Schnee und Wüste -1 Zahl");
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        if (context.getOpposingFightingArmy(soldier).stream().anyMatch(it -> it instanceof Monster)) {
            power.changeNumberPlus(1);
        }
        if (LocationUtils.isAreaType(context.getLocation(), AreaType.SNOW, AreaType.DESERT)) {
            power.changeNumberPlus(-1);
        }
        return power;
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Waldläuferin");
        soldier.setType(NT_Unit.TYPE_FEMALE);
        soldier.setIcon(114);
        return soldier;
    }


    @Override
    public Soldier createCommander() {
        Soldier commander = new Soldier(owner);
        commander.setCommander(true);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Elfenschützin");
        commander.setType(NT_Unit.TYPE_FEMALE);
        commander.setIcon(115);
        return commander;
    }

}
