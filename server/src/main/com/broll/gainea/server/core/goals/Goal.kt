package com.broll.gainea.server.core.goals

import com.broll.gainea.net.NT_Event_FinishedGoal
import com.broll.gainea.net.NT_Goal
import com.broll.gainea.net.NT_GoalProgression
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.sendUpdate

abstract class Goal(var difficulty: GoalDifficulty, var text: String) : GameUpdateReceiverAdapter() {
    protected val locations = mutableListOf<Location>()
    var restrictionInfo: String? = null
        private set
    var id = 0
        private set
    private var progression = 0
    protected var progressionGoal = NT_Goal.NO_PROGRESSION_GOAL
    private val requiredExpansions = mutableListOf<ExpansionType>()
    protected lateinit var game: Game
    protected lateinit var player: Player
    private var finished = false
    open fun init(game: Game, player: Player): Boolean {
        this.game = game
        this.player = player
        id = game.newObjectId()
        return validForGame()
    }

    protected fun setCustomRestrictionInfo(restrictionInfo: String) {
        this.restrictionInfo = restrictionInfo
    }

    protected fun setExpansionRestriction(vararg expansions: ExpansionType) {
        requiredExpansions.clear()
        requiredExpansions += expansions
        restrictionInfo = expansions.map { it.expansionName }.joinToString(", ")
    }

    protected open fun validForGame(): Boolean {
        if (requiredExpansions.isEmpty()) {
            return true
        }
        return game.map.activeExpansionTypes.containsAll(requiredExpansions)
    }

    @Synchronized
    protected fun success() {
        if (!finished) {
            player.goalHandler.removeGoal(this)
            val nt = NT_Event_FinishedGoal()
            nt.sound = "fanfare.ogg"
            nt.player = player.serverPlayer.id
            nt.goal = nt()
            game.sendUpdate(nt)
            ProcessingUtils.pause(4000)
            player.goalHandler.addPoints(difficulty.points)
            finished = true
            if (!game.isGameOver) {
                //give new goal to player
                game.goalStorage.assignNewRandomGoal(player)
            }
        }
    }

    protected fun updateProgression(progression: Int) {
        if (this.progression != progression && progressionGoal != NT_Goal.NO_PROGRESSION_GOAL) {
            this.progression = progression
            val nt = NT_GoalProgression()
            nt.progression = progression
            nt.index = player.goalHandler.goals.indexOf(this)
            player.serverPlayer.sendTCP(nt)
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
        goal.locations = locations.map { it.number }.toIntArray()
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
