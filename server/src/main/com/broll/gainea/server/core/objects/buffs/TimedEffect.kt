package com.broll.gainea.server.core.objects.buffs

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter

open class TimedEffect : GameUpdateReceiverAdapter() {
    private lateinit var game: Game
    private var owner: Player? = null
    private var forThisTurn = false
    private var rounds = 0
    override fun turnStarted(player: Player) {
        if (owner === player || forThisTurn) {
            decrease()
        }
    }

    override fun roundStarted() {
        if (owner == null) {
            decrease()
        }
    }

    private fun decrease() {
        rounds--
        if (rounds <= 0) {
            unregister()
        }
    }

    protected open fun unregister() {
        game.updateReceiver.unregister(this)
    }

    companion object {
        fun forCurrentTurn(game: Game, effect: TimedEffect) {
            effect.game = game
            effect.forThisTurn = true
            game.updateReceiver.register(effect)
        }

        fun forCurrentRound(game: Game, effect: TimedEffect) {
            effect.game = game
            game.updateReceiver.register(effect)
        }

        fun forPlayersTurn(game: Game, player: Player, effect: TimedEffect) {
            effect.game = game
            effect.owner = player
            game.updateReceiver.register(effect)
        }

        fun forPlayerRounds(game: Game, player: Player, rounds: Int, effect: TimedEffect) {
            effect.game = game
            effect.rounds = rounds
            effect.owner = player
            game.updateReceiver.register(effect)
        }

        fun forGameRounds(game: Game, rounds: Int, effect: TimedEffect) {
            effect.game = game
            effect.rounds = rounds
            game.updateReceiver.register(effect)
        }
    }
}
