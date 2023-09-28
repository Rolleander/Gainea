package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.battle.Battle
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightResult
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.buffs.BuffableInt
import com.broll.gainea.server.core.utils.PlayerUtils
import org.slf4j.LoggerFactory

object BattleSimulation {
    private val Log = LoggerFactory.getLogger(BattleSimulation::class.java)
    private const val SIMULATIONS = 10
    fun calculateRequiredFighters(location: Location, units: List<Unit>, winChance: Float): List<Unit>? {
        val fighters = mutableListOf<Unit>()
        for (unit in units) {
            fighters.add(unit)
            val calculatedWinchance = calculateWinChance(location, fighters)
            if (calculatedWinchance >= winChance) {
                Log.trace("Attacking with " + fighters.size + "/" + units.size + " units should win with " + calculatedWinchance * 100 + "%")
                return fighters
            }
        }
        return null
    }

    fun calculateCurrentWinChance(attackers: List<Unit>, defenders: List<Unit>): Float {
        var wins = 0f
        for (i in 0 until SIMULATIONS) {
            if (winsBattle(attackers, defenders)) {
                wins++
            }
        }
        val winChance = wins / SIMULATIONS.toFloat()
        Log.trace("Continuing attack should win with " + winChance * 100 + "%")
        return winChance
    }

    fun calculateWinChance(location: Location, units: List<Unit>): Float {
        var wins = 0f
        for (i in 0 until SIMULATIONS) {
            if (winsBattle(location, units)) {
                wins++
            }
        }
        return wins / SIMULATIONS.toFloat()
    }

    private fun winsBattle(location: Location, units: List<Unit>): Boolean {
        val owner = PlayerUtils.getOwner(units)
        return winsBattle(units, PlayerUtils.getHostileArmy(owner, location))
    }

    fun winsBattle(attackers: List<Unit>, defenders: List<Unit>): Boolean {
        val simulationWrapper = UnitSimulationWrapper(attackers, defenders)
        while (attackers.any { it.isAlive } && defenders.any { it.isAlive }) {
            val context = BattleContext(attackers, defenders)
            object : Battle(context,
                    attackers.filter { it.isAlive },
                    defenders.filter { it.isAlive }) {
                override fun damage(result: FightResult, source: Unit, target: Unit) {
                    target.takeDamage()
                }
            }.fight()
        }
        val winner = attackers.any { it.isAlive }
        simulationWrapper.restore()
        return winner
    }

    private class UnitSimulationWrapper(attackers: List<Unit>, defenders: List<Unit>) {
        private val units: List<Unit>
        private val originalHealth: List<BuffableInt<MapObject>>

        init {
            units = listOf(attackers, defenders).flatten()
            originalHealth = units.map { it.health }
            units.forEach { it.overwriteHealth(it.health.copy(it)) }
        }

        fun restore() {
            for (i in units.indices) {
                units[i].overwriteHealth(originalHealth[i])
            }
        }
    }
}
