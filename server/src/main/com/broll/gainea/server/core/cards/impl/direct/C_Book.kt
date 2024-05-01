package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.OTHER
import com.broll.gainea.server.core.cards.impl.play.C_PickCard
import com.broll.gainea.server.core.utils.iteratePlayers

class C_Book : DirectlyPlayedCard(
    27, OTHER, "Almanach",
    """Jeder Spieler erhält eine "Arkane Bibliothek"-Karte."""
) {
    init {
        drawChance = 0.7f
    }

    override fun play() {
        game.iteratePlayers(300) {
            it.cardHandler.receiveCard(C_PickCard())
        }
    }
}
