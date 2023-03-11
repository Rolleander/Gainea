package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;

public class KnightsFraction extends Fraction {

    public KnightsFraction() {
        super(FractionType.KNIGHTS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("In Unterzahl +1 Zahl");
        desc.plus("Im 1v1 +2 Zahl");
        desc.contra("Auf Sümpfen -1 Würfel");
        desc.contra("Auf Seen -1 Würfel");
        desc.contra("Auf Bergen -1 Würfel");
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        List<BattleObject> army = context.getFightingMates(soldier);
        List<BattleObject> opponents = context.getOpposingFightingMates(soldier);
        if (army.size() < opponents.size()) {
            // smaller army +1 Z
            power.changeNumberPlus(1);
        } else if (army.size() == 1 && opponents.size() == 1) {
            //1v1  +2 Z
            power.changeNumberPlus(2);
        }
        if (LocationUtils.isAreaType(context.getLocation(), AreaType.BOG, AreaType.LAKE, AreaType.MOUNTAIN)) {
            // -1 W
            power.changeDiceNumber(-1);
        }
        return power;
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Kreuzritter");
        soldier.setIcon(11);
        return soldier;
    }


    @Override
    public Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Kreuzritterchampion");
        commander.setIcon(7);
        return commander;
    }

}
