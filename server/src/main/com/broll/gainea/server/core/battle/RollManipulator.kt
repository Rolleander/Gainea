package com.broll.gainea.server.core.battle

import java.util.function.Consumer

class RollManipulator {
    private val manipulators = mutableListOf<IRollManipulation>()
    fun register(manipulator: IRollManipulation) {
        manipulators.add(manipulator)
    }

    fun roundStarts(attackerRolls: RollResult, defenderRolls: RollResult) {
        manipulators.forEach(Consumer { it: IRollManipulation -> it.roll(attackerRolls, defenderRolls) })
    }
}
