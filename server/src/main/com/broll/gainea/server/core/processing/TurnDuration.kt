package com.broll.gainea.server.core.processing

import com.broll.gainea.server.core.Game
import kotlin.math.max


class TurnDuration(val turns: Int? = 0, val rounds: Int? = 0) {

    private lateinit var game: Game
    private var registeredRound = 0
    private var registeredTurn = 0
    private var progressedTurns = 0
    private var progressedRounds = 0
    private var lastRound = 0
    val completedRounds: Int
        get() {
            var completed = progressedRounds
            if (game.currentTurn < registeredTurn) {
                completed -= 1
            }
            return max(0, completed)
        }

    val completedTurns: Int
        get() = progressedTurns

    fun register(game: Game) {
        this.game = game
        registeredRound = game.rounds
        registeredTurn = game.currentTurn
        lastRound = registeredRound
    }

    fun progress() {
        progressedTurns++
        if (game.rounds != lastRound) {
            progressedRounds++
            lastRound = game.rounds
        }
    }

    fun completed() =
        when {
            turns != null -> completedTurns >= turns
            rounds != null -> completedRounds >= rounds
            else -> true
        }

}

fun thisTurn() = turns(1)
fun turns(turns: Int) = TurnDuration(turns = turns)

fun thisRound() = rounds(1)
fun rounds(rounds: Int) = TurnDuration(rounds = rounds)