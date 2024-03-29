package com.broll.gainea.server.core.goals

import com.broll.gainea.server.core.player.Player

abstract class RoundGoal(difficulty: GoalDifficulty, text: String, private val roundTarget: Int) : Goal(difficulty, text) {
    private var rounds = 0
    private var turns = 0

    init {
        progressionGoal = roundTarget
    }

    override fun turnStarted(player: Player) {
        if (game.currentTurn > 0 || game.rounds > 1) {
            turns++
            if (turns > game.allPlayers.size) {
                check()
            }
        }
    }

    protected fun progressRound() {
        turns = 0
        rounds++
        updateProgression(rounds)
        if (rounds >= roundTarget) {
            success()
        }
    }

    protected fun resetRounds() {
        turns = 0
        rounds = 0
        updateProgression(rounds)
    }
}
