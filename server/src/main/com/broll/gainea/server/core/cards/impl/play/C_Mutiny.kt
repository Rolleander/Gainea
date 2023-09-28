package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.buffs.TimedEffect

class C_Mutiny : Card(15, "Meuterei", "Alle Schiffe sind für " + ROUNDS + " Runden nicht mehr begehbar") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        game.map.allShips.forEach { it.traversable = false }
        TimedEffect.forPlayerRounds(game, owner, ROUNDS, object : TimedEffect() {
            override fun unregister() {
                super.unregister()
                game.map.allShips.forEach { it.traversable = true }
            }
        })
    }

    companion object {
        private const val ROUNDS = 2
    }
}
