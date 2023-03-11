package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.UnitControl;

public class DruidsFraction extends Fraction {

    private static float SPAWN_CHANCE = 0.4f;

    public DruidsFraction() {
        super(FractionType.DRUIDS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Gefallene Einheiten können zu unbeweglichen Wurzelgolems (1/2) werden");
        desc.contra("Auf Schiffen -1 Zahl");
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        if (context.getLocation() instanceof Ship) {
            power.changeNumberPlus(-1);
        }
        return power;
    }

    @Override
    public void killed(Unit unit, BattleResult throughBattle) {
        if (unit.getOwner() == owner) {
            druidDied(unit);
        }
    }

    private void druidDied(Unit unit) {
        if (!(unit instanceof Tree) && RandomUtils.randomBoolean(SPAWN_CHANCE)) {
            Tree tree = new Tree(owner);
            UnitControl.spawn(game, tree, unit.getLocation());
        }
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Druidin");
        soldier.setIcon(110);
        soldier.setType(NT_Unit.TYPE_FEMALE);
        return soldier;
    }

    @Override
    public Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Druidenhaupt Zerus");
        commander.setIcon(102);
        return commander;
    }

    private class Tree extends Soldier {
        public Tree(Player owner) {
            super(owner);
            setStats(1, 2);
            setName("Wurzelgolem");
            setIcon(99);
            //cant move or attack
            getAttacksPerTurn().setValue(0);
            getMovesPerTurn().setValue(0);
        }
    }
}
