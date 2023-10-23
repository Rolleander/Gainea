package com.broll.gainea.server.core.battle

import com.broll.gainea.server.core.player.Player

class RollManipulator {
    private val manipulators = mutableListOf<(RollingRound) -> Unit>()
    fun register(manipulator: (RollingRound) -> Unit) {
        manipulators.add(manipulator)
    }

    fun roundStarts(context: BattleContext, attackerRolls: RollResult, defenderRolls: RollResult) {
        val round = RollingRound(context, attackerRolls, defenderRolls)
        manipulators.forEach { it(round) }
    }

}

data class RollingRound(val context: BattleContext, val attacker: RollResult, val defender: RollResult) {

    fun getRollsSide(player: Player): RollResult? {
        if (context.isAttacker(player)) {
            return attacker
        } else if (context.isDefender(player)) {
            return defender;
        }
        return null;
    }

    fun getOpposingRollsSide(player: Player): RollResult? {
        if (context.isAttacker(player)) {
            return defender
        } else if (context.isDefender(player)) {
            return attacker;
        }
        return null;
    }
}