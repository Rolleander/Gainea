package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Monster;
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
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (fighters.size() < enemies.size()) {
            // smaller army +1 Z
            power.changeNumberPlus(1);
        } else if (fighters.size() == enemies.size() && fighters.size() == 1) {
            //1v1  +2 Z
            power.changeNumberPlus(2);
        }
        if (LocationUtils.isAreaType(location, AreaType.BOG,AreaType.LAKE,AreaType.MOUNTAIN)) {
            // -1 W
            power.changeDiceNumber(-1);
        }
        return power;
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Kreuzritter");
        soldier.setIcon(11);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Kreuzritterchampion");
        commander.setIcon(7);
    }

}
