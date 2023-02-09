package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;

public class SamuraiFraction extends Fraction {
    public SamuraiFraction() {
        super(FractionType.SAMURAI);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Als Angreifer +1 Zahl");
        desc.plus("Auf Bergen +1 Würfel");
        desc.contra("Als Verteidiger höchste Würfelzahl 5");
        return desc;
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (isAttacker) {
            power.changeNumberPlus(1);
        } else {
            power.setHighestNumber(5);
        }
        if (LocationUtils.isAreaType(location, AreaType.MOUNTAIN)) {
            power.changeDiceNumber(1);
        }
        return power;
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Samurai");
        soldier.setIcon(111);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Ronin");
        commander.setIcon(113);
    }


}
