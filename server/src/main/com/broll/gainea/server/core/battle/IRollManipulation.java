package com.broll.gainea.server.core.battle;

public interface IRollManipulation {

    void roll(BattleContext context, RollResult attackerRolls, RollResult defenderRolls);
}
