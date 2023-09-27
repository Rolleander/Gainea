package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Shipimport com.broll.gainea.server.core.objects.buffs.TimedEffectimport java.util.function.Consumer
class C_Mutiny : Card(15, "Meuterei", "Alle Schiffe sind für " + ROUNDS + " Runden nicht mehr begehbar") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        game.map.allShips.forEach(Consumer { it: Ship? -> it.setTraversable(false) })
        TimedEffect.Companion.forPlayerRounds(game!!, owner, ROUNDS, object : TimedEffect() {
            override fun unregister() {
                super.unregister()
                game.map.allShips.forEach(Consumer { it: Ship? -> it.setTraversable(true) })
            }
        })
    }

    companion object {
        private const val ROUNDS = 2
    }
}
