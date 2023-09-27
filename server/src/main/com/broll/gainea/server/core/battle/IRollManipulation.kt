package com.broll.gainea.server.core.battle

interface IRollManipulation {
    fun roll(attackerRolls: RollResult?, defenderRolls: RollResult?)
}
