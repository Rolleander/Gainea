package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;

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
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        Location location = context.getLocation();
        if (location instanceof Ship || LocationUtils.isAreaType(location, AreaType.SNOW)) {
            power.changeNumberPlus(1);
        }
        if (LocationUtils.isAreaType(location, AreaType.DESERT)) {
            power.changeNumberPlus(-2);
        }
        return power;
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new VikingSoldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Wikinger");
        soldier.setIcon(106);
        return soldier;
    }


    @Override
    public Soldier createCommander() {
        Soldier commander = new VikingSoldier(owner);
        commander.setCommander(true);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Jarl Olaf");
        commander.setIcon(104);
        return commander;
    }

    private static class VikingSoldier extends Soldier {

        public VikingSoldier(Player owner) {
            super(owner);
        }

        @Override
        public boolean canMoveTo(Location to) {
            return to.isTraversable();
        }
    }


}
