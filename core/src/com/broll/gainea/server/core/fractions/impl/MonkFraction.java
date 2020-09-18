package com.broll.gainea.server.core.fractions.impl;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
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
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

public class MonkFraction extends Fraction {
    public MonkFraction() {
        super(FractionType.MONKS);
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (enemies.stream().map(it -> it instanceof Monster).reduce(true, Boolean::logicalAnd)) {
            power.changeDiceNumber(-2);
        }
        return power;
    }

    @Override
    public void turnStarted(ActionHandlers actionHandlers) {
        if (!owner.getUnits().isEmpty()) {
            BattleObject randomUnit = owner.getUnits().get(MathUtils.random(owner.getUnits().size()-1));
            //inc life
            randomUnit.setMaxHealth(randomUnit.getMaxHealth() + 1);
            //heal
            UnitControl.heal(game, randomUnit, 1);
        }
        super.turnStarted(actionHandlers);
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Mönch");
        soldier.setIcon(108);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Großmeister Eron");
        commander.setIcon(107);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Jede Runde erhält eine Einheit +1 Leben");
        desc.contra("Gegen wilde Monster -2 Würfel");
        return desc;
    }
}
