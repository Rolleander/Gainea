package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.player.Player;

public class GuardsFraction extends Fraction {

    public GuardsFraction() {
        super(FractionType.GUARDS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Als Verteidiger ist die niedrigste Würfelzahl 3");
        desc.plus("Zahl +1 für Verteidiger, die ihr Feld mindestens eine Runde\nnicht verlassen haben");
        desc.contra("Als Angreifer Zahl -1");
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        if (context.isAttacking(soldier)) {
            power.changeNumberPlus(-1);
        } else {
            power.withLowestNumber(3);
        }
        return power;
    }


    @Override
    public Soldier createSoldier() {
        Soldier soldier = new GuardSoldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Gardistenwache");
        soldier.setIcon(19);
        return soldier;
    }


    @Override
    public Soldier createCommander() {
        Soldier commander = new GuardSoldier(owner);
        commander.setCommander(true);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Elitegardist");
        commander.setIcon(15);
        return commander;
    }

    private static class GuardSoldier extends Soldier {

        private IntBuff buff;
        private Location lastLocation;

        public GuardSoldier(Player owner) {
            super(owner);
        }

        @Override
        public void turnStart() {
            if (getLocation() == lastLocation) {
                buff = new IntBuff(BuffType.ADD, 1);
                getNumberPlus().addBuff(buff);
            } else {
                lastLocation = getLocation();
                clearBuff();
            }
            super.turnStart();
        }

        private void clearBuff() {
            if (buff != null) {
                buff.remove();
                buff = null;
            }
        }

        @Override
        public FightingPower calcFightingPower(BattleContext context) {
            if (context.isAttacking(this)) {
                clearBuff();
            }
            return super.calcFightingPower(context);
        }
    }

}
