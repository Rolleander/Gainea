package com.broll.gainea.server.core.battle;

import java.util.ArrayList;
import java.util.List;

public class RollManipulator {

    private final List<IRollManipulation> manipulators = new ArrayList<>();

    public void register(IRollManipulation manipulator) {
        this.manipulators.add(manipulator);
    }

    void roundStarts(RollResult attackerRolls, RollResult defenderRolls) {
        this.manipulators.forEach(it -> it.roll(attackerRolls, defenderRolls));
    }

}
