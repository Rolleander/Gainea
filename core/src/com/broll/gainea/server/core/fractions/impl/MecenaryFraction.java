package com.broll.gainea.server.core.fractions.impl;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;

import java.util.List;

public class MecenaryFraction extends Fraction {

    private final static int[] ICONS = new int[]{2, 4, 8, 9, 13, 14, 17, 20, 26, 27, 29, 30, 31, 32, 34, 36, 37, 38, 39, 40, 41, 43};
    private int turns;
    private final static int SPAWN_TURN = 2;

    public MecenaryFraction() {
        super(FractionType.MERCENARY);
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        power.setHighestNumber(4);
        power.setLowestNumber(2);
        return power;
    }

    @Override
    public void prepareTurn(ActionHandlers actionHandlers) {
        super.turnStarted(actionHandlers);
        turns++;
        if (turns >= SPAWN_TURN) {
            //spawn another soldier
            super.prepareTurn(actionHandlers);
            turns = 0;
        }
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Söldner");
        soldier.setIcon(ICONS[MathUtils.random(ICONS.length-1)]);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Söldnerkommandant");
        commander.setIcon(5);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Jeden zweiten Zug erhaltet Ihr einen weiteren Soldat");
        desc.plus("Minimale Zahl beim Würfeln ist 2");
        desc.contra("Maximale Zahl beim Würfeln ist 4");
        return desc;
    }
}
