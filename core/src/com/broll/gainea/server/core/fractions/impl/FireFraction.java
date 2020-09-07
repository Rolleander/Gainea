package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;

public class FireFraction extends Fraction {

    public FireFraction() {
        super(FractionType.FIRE);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Ab 3 Kriegern ");
        desc.contra("");
        return desc;
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (LocationUtils.isAreaType(location, AreaType.DESERT) && fighters.size() > 2) {
            power.addDices(1);
        }
        return power;
    }

    @Override
    public void killedMonster(Monster monster) {
        if (LocationUtils.isAreaType(monster.getLocation(), AreaType.SNOW)) {
            //no card when monster on ice
            return;
        }
        super.killedMonster(monster);
    }
}
