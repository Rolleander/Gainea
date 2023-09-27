package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.GameContainerimport

com.broll.gainea.server.core.bot.BotUtilsimport com.broll.gainea.server.core.goals.Goalimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.PlayerUtilsimport org.apache.commons.lang3.tuple.MutablePairimport java.util.function.Consumerimport java.util.stream.Collectors
class GoalStrategy(val botStrategy: BotStrategy, val goal: Goal?, private val player: Player?, private val game: GameContainer?, private val constants: StrategyConstants) {
    val units: MutableList<Unit?> = ArrayList()
    var targetLocations: Set<Location?>? = HashSet()
        private set
    private var prepareStrategy: IPrepareStrategy? = null
    private var requiredUnits = 0
    var isSpreadUnits = true

    init {
        goal?.botStrategy(this)
    }

    fun allowFighting(unit: Unit?): Boolean {
        val location = unit.getLocation()
        if (!targetLocations!!.contains(location)) {
            return true
        }
        val lowestOccupation = lowesOccupations
        val occupiers = getOccupyingUnits(location)
        val occupierCount = occupiers.size
        val expendable = occupierCount - Math.max(1, lowestOccupation)
        val index = occupiers.indexOf(unit)
        return index < expendable && expendable > 0
    }

    private fun getOccupyingUnits(location: Location?): List<Unit?> {
        return units.stream().filter { it: Unit? -> it.getLocation() === location }.collect(Collectors.toList())
    }

    fun updateTargets(targetLocations: Set<Location?>?) {
        if (this.targetLocations!!.isEmpty() && !targetLocations!!.isEmpty()) {
            botStrategy.restrategizeUnits(botStrategy.fallbackStrategy)
        }
        this.targetLocations = targetLocations
        units.forEach(Consumer { it: Unit? -> botStrategy.moveTargets.remove(it) })
        requiredUnits = Math.max(requiredUnits, targetLocations!!.size)
        if (targetLocations.isEmpty()) {
            botStrategy.restrategizeUnits(this)
        }
    }

    fun setPrepareStrategy(prepareStrategy: IPrepareStrategy?) {
        this.prepareStrategy = prepareStrategy
    }

    fun setRequiredUnits(requiredUnits: Int) {
        this.requiredUnits = requiredUnits
    }

    fun prepare() {
        if (prepareStrategy != null) {
            prepareStrategy!!.prepare()
        }
    }

    fun requiresMoreUnits(): Boolean {
        return units.size < requiredUnits && !targetLocations!!.isEmpty()
    }

    fun strategizeUnit(unit: Unit?) {
        units.add(unit)
    }

    fun getClosestDistance(unit: Unit?, location: Location?): Int {
        return BotUtils.getBestPath(unit, location, targetLocations).value
    }

    fun scoreLocations(unit: Unit?, locationScores: Map<Location?, MutablePair<GoalStrategy?, Int>>) {
        for (location in locationScores.keys) {
            val entry = locationScores[location]!!
            val score = getClosestDistance(unit, location)
            if (score < entry.value) {
                entry.setLeft(this)
                entry.setRight(score)
            }
        }
    }

    val lowesOccupations: Int
        get() = targetLocations!!.stream().mapToInt { it: Location? -> PlayerUtils.getUnits(player, it).size }.min().orElse(0)

    override fun toString(): String {
        return if (goal != null) {
            goal.text
        } else "fallbackstrategy"
    }
}
