package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.GameContainerimport

com.broll.gainea.server.core.bot.BotUtilsimport com.broll.gainea.server.core.goals.Goalimport org.apache.commons.lang3.tuple.MutablePair com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.init.ServerSetup
import com.broll.gainea.server.ServerStatisticimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Player
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListenerimport

org.slf4j.LoggerFactoryimport java.util.ArrayListimport java.util.HashMapimport java.util.function.Consumerimport java.util.function.Functionimport java.util.stream.Collectors
class BotStrategy(private val game: GameContainer?, private val player: Player?, val constants: StrategyConstants) {
    private val goalStrategies: MutableList<GoalStrategy?> = ArrayList()
    private val strategizedUnits: MutableMap<Unit?, GoalStrategy?> = HashMap()
    val moveTargets: MutableMap<Unit?, Location?> = HashMap()
    val fallbackStrategy: GoalStrategy?

    init {
        fallbackStrategy = FallbackStrategy.create(this, player, game, constants)
    }

    fun getStrategy(unit: Unit?): GoalStrategy? {
        return strategizedUnits[unit]
    }

    fun getGoalStrategies(): List<GoalStrategy?> {
        return goalStrategies
    }

    private fun updateUnitStrategies() {
        for (unit in player.getUnits()) {
            val strategy = strategizedUnits[unit]
            if (strategy == null || strategy.targetLocations.isEmpty()) {
                strategizeUnit(unit)
            }
        }
    }

    private fun strategizeUnit(unit: Unit?) {
        val targetGoals = goalStrategies.stream().filter { it: GoalStrategy? -> !it.getTargetLocations().isEmpty() }.collect(Collectors.toList())
        val needyGoals = targetGoals.stream().filter { obj: GoalStrategy? -> obj!!.requiresMoreUnits() }.collect(Collectors.toList())
        if (!needyGoals.isEmpty()) {
            val goal = needyGoals[BotUtils.getLowestScoreIndex(needyGoals
            ) { it: GoalStrategy? -> it!!.getClosestDistance(unit, unit.getLocation()) }]
            strategizeUnit(goal, unit)
            return
        }
        if (targetGoals.isEmpty()) {
            strategizeUnit(fallbackStrategy, unit)
        } else {
            strategizeUnit(RandomUtils.pickRandom(targetGoals), unit)
        }
    }

    private fun strategizeUnit(goal: GoalStrategy?, unit: Unit?) {
        Log.trace("Strategize $unit to goal $goal")
        goal!!.strategizeUnit(unit)
        strategizedUnits[unit] = goal
    }

    fun prepareTurn() {
        updateUnitStrategies()
        goalStrategies.forEach(Consumer { goal: GoalStrategy? ->
            val deadUnits = goal.getUnits().stream().filter { obj: Unit? -> obj!!.isDead }.collect(Collectors.toList())
            deadUnits.forEach(Consumer { unit: Unit? ->
                strategizedUnits.remove(unit)
                moveTargets.remove(unit)
            })
            goal.getUnits().removeAll(deadUnits)
            goal!!.prepare()
        })
    }

    fun synchronizeGoalStrategies() {
        val goals = player.getGoalHandler().goals
        goals!!.forEach(Consumer { goal: Goal? ->
            if (goalStrategies.stream().noneMatch { it: GoalStrategy? -> it.getGoal() === goal }) {
                goalStrategies.add(GoalStrategy(this, goal, player, game, constants))
            }
        })
        for (strategy in goalStrategies) {
            if (!goals!!.contains(strategy.getGoal())) {
                clearGoal(strategy)
            }
        }
    }

    fun restrategizeUnits(goal: GoalStrategy?) {
        goal.getUnits().forEach(Consumer { it: Unit? ->
            strategizedUnits.remove(it)
            moveTargets.remove(it)
        })
        goal.getUnits().clear()
    }

    private fun clearGoal(goal: GoalStrategy?) {
        goalStrategies.remove(goal)
        restrategizeUnits(goal)
    }

    fun chooseUnitPlace(unit: Unit?, locations: List<Location?>?): Int {
        if (unit.getOwner() !== player) {
            return RandomUtils.random(0, locations!!.size - 1)
        }
        val goals = goalStrategies.stream().filter { obj: GoalStrategy? -> obj!!.requiresMoreUnits() }.collect(Collectors.toList())
        if (goals.isEmpty()) {
            return RandomUtils.random(0, locations!!.size - 1)
        }
        val scores: MutableMap<Location?, MutablePair<GoalStrategy?, Int>> = HashMap()
        locations!!.forEach(Consumer { it: Location? -> scores[it] = MutablePair.of(null, Int.MAX_VALUE) })
        goals.forEach(Consumer { it: GoalStrategy? -> it!!.scoreLocations(unit, scores) })
        val (key, value) = BotUtils.getLowestScoreEntry<Map.Entry<Location, MutablePair<GoalStrategy, Int>>>(ArrayList<Map.Entry<Location?, MutablePair<GoalStrategy?, Int>>>(scores.entries), Function<Map.Entry<Location?, MutablePair<GoalStrategy?, Int>>, Int> { (_, value): Map.Entry<Location?, MutablePair<GoalStrategy?, Int>> -> value.getRight() })
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
