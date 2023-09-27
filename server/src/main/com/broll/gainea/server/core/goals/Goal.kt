package com.broll.gainea.server.core.goals

import com.broll.gainea.net.NT_Event_FinishedGoal
import com.broll.gainea.net.NT_Goal
import com.broll.gainea.net.NT_GoalProgression
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.GameUtils
import com.broll.gainea.server.core.utils.ProcessingUtils
import java.util.Arrays
import java.util.stream.Collectors

abstract class Goal(var difficulty: GoalDifficulty, var text: String?) : GameUpdateReceiverAdapter() {
    protected var locations: List<Location> = ArrayList()
    var restrictionInfo: String? = null
        private set
    var id = 0
        private set
    private var progression = 0
    private var progressionGoal = NT_Goal.NO_PROGRESSION_GOAL
    private var requiredExpansions: Array<ExpansionType>?
    protected var game: GameContainer? = null
    protected var player: Player? = null
    private var finished = false
    open fun init(game: GameContainer, player: Player?): Boolean {
        this.game = game
        this.player = player
        id = game.newObjectId()
        return validForGame()
    }

    protected fun setProgressionGoal(goal: Int) {
        progressionGoal = goal
    }

    protected fun setCustomRestrictionInfo(restrictionInfo: String?) {
        this.restrictionInfo = restrictionInfo
    }

    protected fun setExpansionRestriction(vararg expansions: ExpansionType) {
        requiredExpansions = expansions
        restrictionInfo = Arrays.stream(expansions).map { obj: ExpansionType -> obj.getName() }.collect(Collectors.joining(","))
    }

    protected open fun validForGame(): Boolean {
        if (requiredExpansions == null) {
            return true
        }
        val activeExpansions = game.getMap().activeExpansionTypes
        for (type in requiredExpansions!!) {
            if (!activeExpansions!!.contains(type)) {
                //required expansion of goal is not active in this game
                return false
            }
        }
        return true
    }

    @Synchronized
    protected fun success() {
        if (!finished) {
            player.getGoalHandler().removeGoal(this)
            val nt = NT_Event_FinishedGoal()
            nt.sound = "fanfare.ogg"
            nt.player = player.getServerPlayer().id
            nt.goal = nt()
            GameUtils.sendUpdate(game, nt)
            ProcessingUtils.pause(4000)
            player.getGoalHandler().addPoints(difficulty.points)
            finished = true
            if (!game!!.isGameOver) {
                //give new goal to player
                game.getGoalStorage().assignNewRandomGoal(player)
            }
        }
    }

    protected fun updateProgression(progression: Int) {
        if (this.progression != progression && progressionGoal != NT_Goal.NO_PROGRESSION_GOAL) {
            this.progression = progression
            val nt = NT_GoalProgression()
            nt.progression = progression
            nt.index = player.getGoalHandler().goals.indexOf(this)
            player.getServerPlayer().sendTCP(nt)
        }
    }

    abstract fun check()
    fun nt(): NT_Goal {
        val goal = NT_Goal()
        goal.id = id
        goal.description = text
        goal.points = difficulty.points
        goal.restriction = restrictionInfo
        goal.progression = progression
        goal.progressionGoal = progressionGoal
        goal.locations = locations.stream().mapToInt { obj: Location -> obj.number }.toArray()
        return goal
    }

    abstract fun botStrategy(strategy: GoalStrategy)
    override fun toString(): String {
        return super.toString() + "{" +
                "text='" + text + '\'' +
                ", difficulty=" + difficulty +
                ", restrictionInfo='" + restrictionInfo + '\'' +
                ", player=" + player +
                '}'
    }
}
