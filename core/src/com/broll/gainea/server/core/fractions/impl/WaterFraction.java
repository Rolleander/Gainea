package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;

public class WaterFraction extends Fraction {

    public WaterFraction() {
        super(FractionType.WATER);
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
        soldier.setName("Wassermagier");
        soldier.setIcon(46);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Frostbeschwörer Arn");
        commander.setIcon(116);
    }

    private class IceSummon extends Monster{

        public IceSummon(){
            setIcon(117);
            setName("Eiskoloss");
            setStats(2,4);
        }
    }


}
