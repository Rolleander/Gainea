package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;

public class LizardFraction extends Fraction {

    private int turns;
    private final static int SPAWN_TURN = 2;

    public LizardFraction() {
        super(FractionType.LIZARDS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Einheiten können Laufen und Angreifen im gleichen Zug");
        desc.contra("Erhält nur jeden zweiten Zug einen Soldat");
        desc.contra("Im Schnee -1 Zahl");
        return desc;
    }

    @Override
    public void prepareTurn(ActionHandlers actionHandlers) {
        turns++;
        if (turns >= SPAWN_TURN || owner.getControlledLocations().isEmpty()) {
            //spawn  soldier
            super.prepareTurn(actionHandlers);
            turns = 0;
        }
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        if (LocationUtils.isAreaType(area, AreaType.SNOW)) {
            power.changeNumberPlus(-1);
        }
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(2, 2);
        soldier.setName("Echsenkrieger");
        soldier.setIcon(122);
        soldier.setMoveOrAttackRestriction(false);
        return soldier;
    }


    @Override
    public Soldier createCommander() {
        Soldier commander = new Soldier(owner);
        commander.setCommander(true);
        commander.setStats(3, 5);
        commander.setName("Grash der Vernichter");
        commander.setIcon(123);
        commander.setMoveOrAttackRestriction(false);
        return commander;
    }

}
