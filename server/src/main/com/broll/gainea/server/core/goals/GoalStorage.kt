package com.broll.gainea.server.core.goals

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.init.GoalTypes
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

class GoalStorage(private val game: Game,
                  private val goalTypes: GoalTypes) {
    private val goalClasses = mutableListOf<Class<out Goal>>()
    private val loader: PackageLoader<Goal> = PackageLoader(Goal::class.java, PACKAGE_PATH)

    init {
        initGoals()
    }

    private fun initGoals() {
        goalClasses.clear()
        goalClasses += loader.classes.stream().collect(Collectors.toList())
        goalClasses.shuffle()
    }

    fun assignNewRandomGoal(player: Player) {
        assignNewGoal(player) { true }
    }

    fun assignNewGoal(player: Player, difficulty: GoalDifficulty) {
        assignNewGoal(player) { it.difficulty == difficulty }
    }

    fun getAnyGoals(player: Player, count: Int): List<Goal> {
        val goals = mutableListOf<Goal>()
        for (clazz in loader.classes.shuffled()) {
            val goal = loader.instantiate(clazz)
            if (goal.init(game, player) && player.goalHandler.isValidNewGoal(goal)) {
                goals.add(goal)
                if (goals.size == count) {
                    break
                }
            }
        }
        return goals
    }

    fun assignNewGoal(player: Player, condition: (Goal) -> Boolean) {
        getGoal(player, condition)?.let {
            player.goalHandler.newGoal(it)
        }
    }

    private fun getGoal(player: Player, condition: (Goal) -> Boolean): Goal? {
        var goal = newGoal(player, condition)
        if (goal != null) {
            return goal
        } else {
            //no goal found for condition
            if (goalClasses.isEmpty()) {
                //refresh goals
                initGoals()
                //try again to find one
                goal = newGoal(player, condition)
                if (goal != null) {
                    return goal
                } else {
                    Log.error("No goal for condition $condition was found")
                }
            } else {
                Log.error("No goal for condition $condition was found in remaining goals")
            }
        }
        return null
    }

    private fun newGoal(forPlayer: Player, condition: (Goal) -> Boolean): Goal? {
        for (clazz in goalClasses) {
            val goal = loader.instantiate(clazz)
            if (goalTypes.contains(goal.difficulty)) {
                if (goal.init(game, forPlayer) && forPlayer.goalHandler.isValidNewGoal(goal)) {
                    if (condition(goal)) {
                        goalClasses.remove(clazz)
                        return goal
                    }
                }
            }
        }
        //no more matching goals found
        return null
    }

    companion object {
        private val Log = LoggerFactory.getLogger(GoalStorage::class.java)
        private const val PACKAGE_PATH = "com.broll.gainea.server.core.goals.impl"
    }
}
