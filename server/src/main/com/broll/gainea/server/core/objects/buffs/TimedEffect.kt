package com.broll.gainea.server.core.objects.buffs

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter

open class TimedEffect : GameUpdateReceiverAdapter() {
    protected lateinit var game: GameContainer
    protected lateinit var owner: Player
    protected var forThisTurn = false
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
        fun forCurrentTurn(game: GameContainer, effect: TimedEffect) {
            effect.game = game
            effect.forThisTurn = true
            game.updateReceiver.register(effect)
        }

        fun forCurrentRound(game: GameContainer, effect: TimedEffect) {
            effect.game = game
            game.updateReceiver.register(effect)
        }

        fun forPlayersTurn(game: GameContainer, player: Player, effect: TimedEffect) {
            effect.game = game
            effect.owner = player
            game.updateReceiver.register(effect)
        }

        fun forPlayerRounds(game: GameContainer, player: Player, rounds: Int, effect: TimedEffect) {
            effect.game = game
            effect.rounds = rounds
            effect.owner = player
            game.updateReceiver.register(effect)
        }

        fun forGameRounds(game: GameContainer, rounds: Int, effect: TimedEffect) {
            effect.game = game
            effect.rounds = rounds
            game.updateReceiver.register(effect)
        }
    }
}
