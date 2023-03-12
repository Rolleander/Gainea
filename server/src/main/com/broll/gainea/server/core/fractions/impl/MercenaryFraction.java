package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.objects.Soldier;

public class MercenaryFraction extends Fraction {

    private final static int[] ICONS = new int[]{2, 4, 8, 9, 13, 14, 17, 20, 26, 27, 29, 30, 31, 32, 34, 36, 37, 38, 39, 40, 41, 43};
    private int turns;
    private final static int SPAWN_TURN = 2;

    public MercenaryFraction() {
        super(FractionType.MERCENARY);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Jeden zweiten Zug erhaltet Ihr einen weiteren Soldat");
        desc.plus("Minimale Zahl beim Würfeln ist 2");
        desc.contra("Maximale Zahl beim Würfeln ist 5");
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        return super.calcFightingPower(soldier, context).withHighestNumber(5).withLowestNumber(2);
    }

    @Override
    public void prepareTurn(ActionHandlers actionHandlers) {
        super.prepareTurn(actionHandlers);
        turns++;
        if (turns >= SPAWN_TURN) {
            //spawn another soldier
            super.prepareTurn(actionHandlers);
            turns = 0;
        }
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Söldner");
        soldier.setIcon(ICONS[RandomUtils.random(ICONS.length - 1)]);
        return soldier;
    }


    @Override
    public Soldier createCommander() {
        Soldier commander = new Soldier(owner);
        commander.setCommander(true);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Söldnerkommandant");
        commander.setIcon(5);
        return commander;
    }
    
}
