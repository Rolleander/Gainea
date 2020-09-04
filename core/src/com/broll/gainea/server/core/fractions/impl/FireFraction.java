package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;

import java.util.List;

public class FireFraction extends Fraction {

    public FireFraction() {
        super(FractionType.FIRE);
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        switch(area.getType()){
            case DESERT:
            case MOUNTAIN:
                power.
                break;
            case SNOW:
            case LAKE:
                break;
        }
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("");
        desc.contra("");
        return desc;
    }
}
