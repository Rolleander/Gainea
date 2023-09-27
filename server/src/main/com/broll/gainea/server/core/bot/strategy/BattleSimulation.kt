package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.battle.Battleimport

com.broll.gainea.server.core.battle.BattleContextimport com.broll.gainea.server.core.battle.FightResultimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffableIntimport com.broll.gainea.server.core.utils.PlayerUtilsimport org.slf4j.LoggerFactoryimport java.util.function.Consumerimport java.util.stream.Collectorsimport java.util.stream.Stream
object BattleSimulation {
    private val Log = LoggerFactory.getLogger(BattleSimulation::class.java)
    private const val SIMULATIONS = 10
    fun calculateRequiredFighters(location: Location?, units: List<Unit?>, winChance: Float): List<Unit?>? {
        val fighters: MutableList<Unit?> = ArrayList()
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

    fun calculateCurrentWinChance(attackers: List<Unit?>?, defenders: List<Unit?>?): Float {
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

    fun calculateWinChance(location: Location?, units: List<Unit?>): Float {
        var wins = 0f
        for (i in 0 until SIMULATIONS) {
            if (winsBattle(location, units)) {
                wins++
            }
        }
        return wins / SIMULATIONS.toFloat()
    }

    private fun winsBattle(location: Location?, units: List<Unit?>): Boolean {
        val owner = PlayerUtils.getOwner(units)
        return winsBattle(units, PlayerUtils.getHostileArmy(owner, location))
    }

    fun winsBattle(attackers: List<Unit?>?, defenders: List<Unit?>?): Boolean {
        val simulationWrapper = UnitSimulationWrapper(attackers, defenders)
        while (attackers!!.stream().anyMatch { obj: Unit? -> obj!!.isAlive } && defenders!!.stream().anyMatch { obj: Unit? -> obj!!.isAlive }) {
            val context = BattleContext(attackers, defenders)
            object : Battle(context,
                    attackers.stream().filter { obj: Unit? -> obj!!.isAlive }.collect(Collectors.toList()),
                    defenders.stream().filter { obj: Unit? -> obj!!.isAlive }.collect(Collectors.toList())) {
                override fun damage(result: FightResult, source: Unit?, target: Unit) {
                    target.takeDamage()
                }
            }.fight()
        }
        val winner = attackers.stream().anyMatch { obj: Unit? -> obj!!.isAlive }
        simulationWrapper.restore()
        return winner
    }

    private class UnitSimulationWrapper(attackers: List<Unit?>?, defenders: List<Unit?>?) {
        private val units: List<Unit?>
        private val originalHealth: List<BuffableInt<MapObject?>?>

        init {
            units = Stream.concat(attackers!!.stream(), defenders!!.stream()).collect(Collectors.toList())
            originalHealth = units.stream().map { obj: Unit? -> obj.getHealth() }.collect(Collectors.toList())
            units.forEach(Consumer { it: Unit? -> it!!.overwriteHealth(it.health.copy()) })
        }

        fun restore() {
            for (i in units.indices) {
                units[i]!!.overwriteHealth(originalHealth[i])
            }
        }
    }
}
