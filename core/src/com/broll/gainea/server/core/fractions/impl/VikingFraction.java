package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;

public class VikingFraction extends Fraction {
    public VikingFraction() {
        super(FractionType.VIKINGS);
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (location instanceof Ship || LocationUtils.isAreaType(location, AreaType.SNOW)) {
            power.changeNumberPlus(1);
        }
        if(LocationUtils.isAreaType(location, AreaType.DESERT)){
            power.changeNumberPlus(-2);
        }
        return power;
    }


    @Override
    protected boolean canMove(Location from, Location to) {
        //can walk ships in both ways
        return true;
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Wikinger");
        soldier.setIcon(106);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Jarl Olaf");
        commander.setIcon(104);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Können Schiffe in jede Richtung gehen");
        desc.plus("Auf Schiffen Zahl +1");
        desc.plus("Auf Eis Zahl +1");
        desc.contra("Auf Wüste Zahl -2");
        return desc;
    }
}
