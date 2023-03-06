package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
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
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Können Schiffe in jede Richtung gehen");
        desc.plus("Auf Schiffen Zahl +1");
        desc.plus("Auf Eis Zahl +1");
        desc.contra("Auf Wüste Zahl -2");
        return desc;
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (location instanceof Ship || LocationUtils.isAreaType(location, AreaType.SNOW)) {
            power.changeNumberPlus(1);
        }
        if (LocationUtils.isAreaType(location, AreaType.DESERT)) {
            power.changeNumberPlus(-2);
        }
        return power;
    }

    @Override
    public Commander createCommander() {
        return new VikingCommander();
    }

    @Override
    public Soldier createSoldier() {
        return new VikingSolider();
    }

    @Override
    protected void initSoldier(Soldier soldier) {
    }

    @Override
    protected void initCommander(Commander commander) {
    }

    private boolean canVikingMove(Location to) {
        return to.isTraversable();
    }

    private class VikingSolider extends Soldier {

        public VikingSolider() {
            super(VikingFraction.this.owner);
            setStats(SOLDIER_POWER, SOLDIER_HEALTH);
            setName("Wikinger");
            setIcon(106);
        }

        @Override
        public boolean canMoveTo(Location to) {
            return canVikingMove(to);
        }
    }

    private class VikingCommander extends Commander {

        public VikingCommander() {
            super(VikingFraction.this.owner);
            setStats(COMMANDER_POWER, COMMANDER_HEALTH);
            setName("Jarl Olaf");
            setIcon(104);
        }

        @Override
        public boolean canMoveTo(Location to) {
            return canVikingMove(to);
        }
    }
}
