package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.CHAOS

class C_Astrology : DirectlyPlayedCard(
    89,
    CHAOS,
    "Astrologie",
    "Die Anzahl der Sterne aller Spieler wird auf $COUNT gesetzt"
) {

    override fun play() {
        game.activePlayers.forEach {
            val delta = COUNT - it.goalHandler.stars
            it.goalHandler.addStars(delta)
        }
    }

    companion object {
        val COUNT = 9
    }
}
