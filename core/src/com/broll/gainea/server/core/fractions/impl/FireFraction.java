package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
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
    public void turnStarted(ActionHandlers actionHandlers) {

    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Feuermagier");
        soldier.setIcon(23);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Flammenschürer Duras");
        commander.setIcon(48);
    }

    @Override
    public void killedMonster(Monster monster) {
        if (LocationUtils.isAreaType(monster.getLocation(), AreaType.SNOW,AreaType.LAKE)) {
            //no card when monster on ice or water
            return;
        }
        super.killedMonster(monster);
    }


}
