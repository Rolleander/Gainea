package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.getUnits
import org.apache.commons.lang3.tuple.MutablePair

class GoalStrategy(val botStrategy: BotStrategy,
                   val goal: Goal,
                   private val player: Player,
                   private val game: Game,
                   private val constants: StrategyConstants) {
    val units = mutableListOf<Unit>()
    val targetLocations = mutableSetOf<Location>()
    private var prepareStrategy: IPrepareStrategy? = null
    private var requiredUnits = 0
    var isSpreadUnits = true

    init {
        goal.botStrategy(this)
    }

    fun allowFighting(unit: Unit): Boolean {
        if (!targetLocations.contains(unit.location)) {
            return true
        }
        val lowestOccupation = lowesOccupations
        val occupiers = getOccupyingUnits(unit.location!!)
        val occupierCount = occupiers.size
        val expendable = occupierCount - Math.max(1, lowestOccupation)
        val index = occupiers.indexOf(unit)
        return index < expendable && expendable > 0
    }

    private fun getOccupyingUnits(location: Location) = units.filter { it.location === location }

    fun updateTargets(targetLocations: Set<Location>) {
        if (this.targetLocations.isEmpty() && targetLocations.isNotEmpty()) {
            botStrategy.restrategizeUnits(botStrategy.fallbackStrategy)
        }
        this.targetLocations.clear()
        this.targetLocations.addAll(targetLocations)
        units.forEach { botStrategy.moveTargets.remove(it) }
        requiredUnits = Math.max(requiredUnits, targetLocations.size)
        if (targetLocations.isEmpty()) {
            botStrategy.restrategizeUnits(this)
        }
    }

    fun setPrepareStrategy(prepareStrategy: IPrepareStrategy) {
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
        return units.size < requiredUnits && !targetLocations.isEmpty()
    }

    fun strategizeUnit(unit: Unit) {
        units.add(unit)
    }

    fun getClosestDistance(unit: Unit, location: Location): Int {
        return BotUtils.getBestPath(unit, location, targetLocations).second
    }

    fun scoreLocations(unit: Unit, locationScores: Map<Location, MutablePair<GoalStrategy?, Int>>) {
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
        get() = targetLocations.minOf { player.getUnits(it).size }

    override fun toString() = goal.toString()
}
