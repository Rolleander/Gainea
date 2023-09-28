package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import org.apache.commons.lang3.tuple.MutablePair
import org.slf4j.LoggerFactory

class BotStrategy(private val game: GameContainer, private val player: Player, val constants: StrategyConstants) {
    private val goalStrategies = mutableListOf<GoalStrategy>()
    private val strategizedUnits = HashMap<Unit, GoalStrategy>()
    val moveTargets = HashMap<Unit, Location>()
    val fallbackStrategy: GoalStrategy

    init {
        fallbackStrategy = FallbackStrategy.create(this, player, game, constants)
    }

    fun getStrategy(unit: Unit): GoalStrategy? {
        return strategizedUnits[unit]
    }

    fun getGoalStrategies(): List<GoalStrategy> {
        return goalStrategies
    }

    private fun updateUnitStrategies() {
        for (unit in player.units) {
            val strategy = strategizedUnits[unit]
            if (strategy == null || strategy.targetLocations.isEmpty()) {
                strategizeUnit(unit)
            }
        }
    }

    private fun strategizeUnit(unit: Unit) {
        val targetGoals = goalStrategies.filter { it.targetLocations.isNotEmpty() }
        val needyGoals = targetGoals.filter { it.requiresMoreUnits() }
        if (needyGoals.isNotEmpty()) {
            val goal = needyGoals[BotUtils.getLowestScoreIndex(needyGoals
            ) { it.getClosestDistance(unit, unit.location) }]
            strategizeUnit(goal, unit)
            return
        }
        if (targetGoals.isEmpty()) {
            strategizeUnit(fallbackStrategy, unit)
        } else {
            strategizeUnit(RandomUtils.pickRandom(targetGoals), unit)
        }
    }

    private fun strategizeUnit(goal: GoalStrategy, unit: Unit) {
        Log.trace("Strategize $unit to goal $goal")
        goal.strategizeUnit(unit)
        strategizedUnits[unit] = goal
    }

    fun prepareTurn() {
        updateUnitStrategies()

        goalStrategies.forEach { goal ->
            val deadUnits = goal.units.filter { unit -> unit.isDead }
            deadUnits.forEach { unit ->
                strategizedUnits.remove(unit)
                moveTargets.remove(unit)
            }
            goal.units.removeAll(deadUnits)
            goal.prepare()
        }
    }

    fun synchronizeGoalStrategies() {
        val goals = player.goalHandler.goals
        goals.forEach { goal ->
            if (goalStrategies.none { it.goal === goal }) {
                goalStrategies.add(GoalStrategy(this, goal, player, game, constants))
            }
        }
        for (strategy in goalStrategies) {
            if (!goals.contains(strategy.goal)) {
                clearGoal(strategy)
            }
        }
    }

    fun restrategizeUnits(goal: GoalStrategy) {
        goal.units.forEach {
            strategizedUnits.remove(it)
            moveTargets.remove(it)
        }
        goal.units.clear()
    }

    private fun clearGoal(goal: GoalStrategy) {
        goalStrategies.remove(goal)
        restrategizeUnits(goal)
    }

    fun chooseUnitPlace(unit: Unit, locations: List<Location>): Int {
        if (unit.owner !== player) {
            return RandomUtils.random(0, locations.size - 1)
        }
        val goals = goalStrategies.filter { it.requiresMoreUnits() }
        if (goals.isEmpty()) {
            return RandomUtils.random(0, locations.size - 1)
        }
        val scores = HashMap<Location, MutablePair<GoalStrategy?, Int>>()
        locations.forEach { scores[it] = MutablePair.of(null, Int.MAX_VALUE) }
        goals.forEach { it.scoreLocations(unit, scores) }
        val (key, value) = BotUtils.getLowestScoreEntry(scores.entries.toList()) { it.value.right }
        val option = locations.indexOf(key)
        var strategy = value.key
        if (strategy == null) {
            strategy = fallbackStrategy
        }
        strategizeUnit(strategy, unit)
        return option
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BotStrategy::class.java)
    }
}
