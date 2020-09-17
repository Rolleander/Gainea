package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Soldier;

import java.util.List;

public class SamuraiFraction extends Fraction {
    public SamuraiFraction() {
        super(FractionType.SAMURAI);
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power= super.calcPower(location, fighters, enemies, isAttacker);

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

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        return desc;
    }
}
