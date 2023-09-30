package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.bot.impl.BotMove
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.getUnits
import org.apache.commons.collections4.map.MultiValueMap


object LocationDanger {
    fun getFleeToScore(owner: Player, location: Location): Int {
        val ownerPower = owner.getUnits(location).sumOf { it.battleStrength }
        val surroundingEnemyPower = location.connectedLocations.flatMap { it.inhabitants }
                .filterIsInstance(Unit::class.java)
                .filter { it.canMoveTo(location) && it.owner !== owner }
                .filter {
                    if (it is Monster) {
                        return@filter it.mightAttackSoon()
                    }
                    true
                }.sumOf { it.battleStrength }
        val diff = Math.min(0, surroundingEnemyPower - ownerPower)
        return BotMove.MOVE_SCORE - diff
    }

    fun getAnnihilationChance(owner: Player, location: Location): Double {
        return location.connectedLocations.maxOf { neighbour ->
            val units = getEnemyUnits(owner, neighbour)
            units.keys.maxOf { enemy: Player? -> getAnnihilationChance(location, owner, enemy, units.getCollection(enemy)) }
        }
    }

    private fun getAnnihilationChance(to: Location, owner: Player, enemy: Player?, units: Collection<Unit>): Double {
        val defenders = owner.getUnits(to)
        val attackers = units.filter { it.canMoveTo(to) }
        if (attackers.isEmpty()) {
            return 0.0
        }
        return if (enemy == null) {
            attackers.filter { it is Monster && it.mightAttackSoon() }
                    .maxOf { BattleSimulation.calculateCurrentWinChance(listOf(it), defenders).toDouble() }
        } else {
            BattleSimulation.calculateCurrentWinChance(attackers, defenders).toDouble()
        }
    }

    private fun getEnemyUnits(owner: Player, location: Location): MultiValueMap<Player?, Unit> {
        val units = MultiValueMap<Player?, Unit>()
        location.inhabitants.filterIsInstance(Unit::class.java).filter { it.owner !== owner }.forEach { unit -> units[unit.owner] = unit }
        return units
    }
}
