package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;

public class SamuraiFraction extends Fraction {
    public SamuraiFraction() {
        super(FractionType.SAMURAI);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Als Angreifer +1 Zahl");
        desc.plus("Auf Bergen +1 Zahl");
        desc.contra("Als Verteidiger höchste Würfelzahl 5");
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        if (context.isAttacker(owner)) {
            power.changeNumberPlus(1);
        } else {
            power.withHighestNumber(5);
        }
        if (LocationUtils.isAreaType(context.getLocation(), AreaType.MOUNTAIN)) {
            power.changeNumberPlus(1);
        }
        return power;
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Samurai");
        soldier.setIcon(111);
        return soldier;
    }


    @Override
    public Soldier createCommander() {
        Soldier commander = new Soldier(owner);
        commander.setCommander(true);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Ronin");
        commander.setIcon(113);
        return commander;
    }


}
