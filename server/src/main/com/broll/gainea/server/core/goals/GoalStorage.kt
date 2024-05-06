package com.broll.gainea.server.core.goals

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.init.GoalTypes
import org.slf4j.LoggerFactory

class GoalStorage(
    private val game: Game,
    private val goalTypes: GoalTypes
) {
    private val loader: PackageLoader<Goal> = PackageLoader(Goal::class.java, PACKAGE_PATH)
    val allGoals: List<Goal> = loader.instantiateAll()

    fun assignNewRandomGoal(player: Player) {
        assignNewGoal(player) { true }
    }

    fun assignNewGoal(player: Player, difficulty: GoalDifficulty) {
        assignNewGoal(player) { it.difficulty == difficulty }
    }

    fun getRandomGoals(player: Player, count: Int): List<Goal> {
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
        newGoal(player, condition)?.let {
            player.goalHandler.newGoal(it)
        }
    }

    private fun newGoal(player: Player, condition: (Goal) -> Boolean): Goal? {
        for (clazz in loader.classes.shuffled()) {
            val goal = loader.instantiate(clazz)
            if (goalTypes.contains(goal.difficulty) &&
                goal.init(game, player) &&
                condition(goal) &&
                player.goalHandler.isValidNewGoal(goal)
            ) {
                return goal
            }
        }
        //no more matching goals found
        return null
    }

    companion object {
        private val Log = LoggerFactory.getLogger(GoalStorage::class.java)
        const val PACKAGE_PATH = "com.broll.gainea.server.core.goals.impl"
    }
}
