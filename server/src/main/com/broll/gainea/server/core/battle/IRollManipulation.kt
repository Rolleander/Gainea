package com.broll.gainea.server.core.battle

fun interface IRollManipulation {
    fun roll(attackerRolls: RollResult, defenderRolls: RollResult)
}
