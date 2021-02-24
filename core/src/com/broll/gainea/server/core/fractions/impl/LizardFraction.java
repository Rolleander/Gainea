package com.broll.gainea.server.core.fractions.impl;

import com.badlogic.gdx.math.MathUtils;
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
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;

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
        desc.contra("Im Schnee -1 Würfel");
        return desc;
    }

    @Override
    public void prepareTurn(ActionHandlers actionHandlers) {
        turns++;
        if (turns >= SPAWN_TURN) {
            //spawn  soldier
            super.prepareTurn(actionHandlers);
            turns = 0;
        }
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        if(LocationUtils.isAreaType(area, AreaType.SNOW)){
            power.changeDiceNumber(-1);
        }
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Echsenkrieger");
        soldier.setPower(2);
        soldier.setHealth(2);
        soldier.setIcon(122);
        soldier.setMoveOrAttackRestriction(false);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Grash der Vernichter");
        commander.setHealth(5);
        commander.setIcon(123);
        commander.setMoveOrAttackRestriction(false);
    }

}
