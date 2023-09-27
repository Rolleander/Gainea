package com.broll.gainea.server.core.goals

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.init.GoalTypes
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.function.Function
import java.util.stream.Collectors

class GoalStorage(private val game: GameContainer, private val actionHandlers: ActionHandlers, goalTypes: GoalTypes?) {
    private var goalClasses: List<Class<out Goal>?>? = null
    private val loader: PackageLoader<Goal>
    private val goalTypes: GoalTypes?

    init {
        loader = PackageLoader(Goal::class.java, PACKAGE_PATH)
        this.goalTypes = goalTypes
        initGoals()
    }

    private fun initGoals() {
        goalClasses = loader.classes.stream().collect(Collectors.toList())
        Collections.shuffle(goalClasses)
    }

    fun assignNewRandomGoal(player: Player?) {
        assignNewGoal(player) { goal: Goal? -> true }
    }

    fun assignNewGoal(player: Player?, difficulty: GoalDifficulty) {
        assignNewGoal(player) { goal: Goal -> goal.getDifficulty() == difficulty }
    }

    fun getAnyGoals(player: Player?, count: Int): List<Goal> {
        val goals: MutableList<Goal> = ArrayList()
        val goalClasses = loader.classes.stream().collect(Collectors.toList())
        Collections.shuffle(goalClasses)
        for (clazz in goalClasses) {
            val goal = loader.instantiate(clazz)
            if (goal.init(game, player)) {
                goals.add(goal)
                if (goals.size == count) {
                    break
                }
            }
        }
        return goals
    }

    fun assignNewGoal(player: Player?, condition: Function<Goal, Boolean>) {
        val goal = getGoal(player, condition)
        player.getGoalHandler().newGoal(goal)
    }

    private fun getGoal(player: Player?, condition: Function<Goal, Boolean>): Goal? {
        var goal = newGoal(player, condition)
        if (goal != null) {
            return goal
        } else {
            //no goal found for condition
            if (goalClasses!!.isEmpty()) {
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

    private fun newGoal(forPlayer: Player?, condition: Function<Goal, Boolean>): Goal? {
        for (clazz in goalClasses!!) {
            val goal = loader.instantiate(clazz)
            if (goalTypes!!.contains(goal.getDifficulty())) {
                if (goal.init(game, forPlayer)) {
                    if (condition.apply(goal)) {
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
