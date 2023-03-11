package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;

public class RangerFraction extends Fraction {

    public RangerFraction() {
        super(FractionType.RANGER);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Gegen wilde Monster +1 Zahl");
        desc.plus("Auf Grasland +1 Würfel");
        desc.contra("Auf Schnee -2 Würfel");
        desc.contra("Auf Wüste -2 Würfel");
        return desc;
    }

    @Override
    public FightingPower calcPower(Location location, List<Unit> fighters, List<Unit> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (enemies.stream().map(it -> it instanceof Monster).reduce(true, Boolean::logicalAnd)) {
            //vs monsters +2 Z
            power.changeNumberPlus(1);
        }
        if (LocationUtils.isAreaType(location, AreaType.PLAINS)) {
            //plains +1 W
            power.changeDiceNumber(1);
        } else if (LocationUtils.isAreaType(location, AreaType.SNOW, AreaType.DESERT)) {
            // -2 W
            power.changeDiceNumber(-2);
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
    public Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Elfenschützin");
        commander.setType(NT_Unit.TYPE_FEMALE);
        commander.setIcon(115);
        return commander;
    }

}
