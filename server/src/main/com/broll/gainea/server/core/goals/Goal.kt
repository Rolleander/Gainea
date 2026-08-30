package com.broll.gainea.server.core.goals

import com.broll.gainea.core.goals.GoalDifficulty
import com.broll.gainea.core.map.ExpansionType
import com.broll.gainea.net.proto.NT_Event
import com.broll.gainea.net.proto.NT_Event_FinishedGoal
import com.broll.gainea.net.proto.NT_Goal
import com.broll.gainea.net.proto.NT_GoalProgression
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.sendUpdate

abstract class Goal(
    var difficulty: GoalDifficulty,
    var text: String,
    var libraryText: String = "",
    val libraryDifficulties: MutableSet<GoalDifficulty> = mutableSetOf()
) :
    GameUpdateReceiverAdapter() {
    protected val locations = mutableListOf<Location>()
    var restrictionInfo: String? = null
        private set
    var id = 0
        private set
    private var progression = 0
    protected var progressionGoal = NO_PROGRESSION_GOAL
    private val requiredExpansions = mutableListOf<ExpansionType>()
    protected lateinit var game: Game
    protected lateinit var player: Player
    private var finished = false

    open fun init(game: Game, player: Player): Boolean {
        this.game = game
        this.player = player
        id = game.newObjectId()
        if (libraryText.isBlank()) {
            libraryText = text
        }
        if (libraryDifficulties.isEmpty()) {
            libraryDifficulties += difficulty
        }
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
            game.sendUpdate(
                NT_Event_FinishedGoal.newBuilder()
                    .setBase(
                        NT_Event.newBuilder().setSound("fanfare.ogg")
                            .setPlayer(player.serverPlayer.id).build()
                    )
                    .setGoal(nt()).build()
            )
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
        if (this.progression != progression && progressionGoal != NO_PROGRESSION_GOAL) {
            this.progression = progression
            player.serverPlayer.send(
                NT_GoalProgression.newBuilder().setProgression(progression)
                    .setIndex(player.goalHandler.goals.indexOf(this)).build()
            )
        }
    }

    protected fun resetProgression() {
        updateProgression(0)
    }

    abstract fun check()
    fun nt(): NT_Goal {
        val builder =
            NT_Goal.newBuilder().setId(id).setDescription(text).setPoints(difficulty.points)
                .setProgression(progression).setProgressionGoal(progressionGoal)
                .addAllLocations(locations.map { it.number })
        restrictionInfo?.let { builder.restriction = it }
        return builder.build()
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

    companion object {
        private const val NO_PROGRESSION_GOAL = -1
    }
}
