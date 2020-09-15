package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;

public class SamuraiFraction extends Fraction {
    public SamuraiFraction() {
        super(FractionType.SAMURAI);
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        switch (area.getType()) {
            case MOUNTAIN:
            case BOG:
                power.addDices(1);
                break;
            case SNOW:
                power.changeHighestNumber(-1);
                break;
        }
    }

    @Override
    protected boolean canMove(Location from, Location to) {
        //can walk ships in both ways
        return true;
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        return desc;
    }
}
