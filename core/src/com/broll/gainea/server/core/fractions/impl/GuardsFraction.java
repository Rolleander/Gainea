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

public class GuardsFraction extends Fraction {

    public GuardsFraction() {
        super(FractionType.GUARDS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Als Verteidiger ist die niedrigste Würfelzahl 3");
        desc.contra("Als Angreifer -2 Würfel");
        return desc;
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (isAttacker) {
            power.changeDiceNumber(-2);
        } else {
            power.setLowestNumber(3);
        }
        return power;
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Gardistenwache");
        soldier.setIcon(19);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Elitegardist");
        commander.setIcon(15);
    }

    @Override
    public void killedMonster(Monster monster) {
        if (LocationUtils.isAreaType(monster.getLocation(), AreaType.SNOW, AreaType.LAKE)) {
            //no card when monster on ice or water
            return;
        }
        super.killedMonster(monster);
    }


}
