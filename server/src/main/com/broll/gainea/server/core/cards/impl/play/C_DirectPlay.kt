package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.CHAOS
import com.broll.gainea.server.core.cards.playCard

class C_DirectPlay :
    Card(
        84,
        CHAOS,
        "Wahl des Druiden",
        "Wählt eine von drei Event-Karten, die direkt ausgespielt wird."
    ) {

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val cards = game.cardStorage.getDirectlyPlayedCards(OPTIONS)
        val card =
            cards[selectHandler.selectObject("Wählt eine Event-Karte", cards.map { it.nt() })]
        card.init(game, owner)
        game.playCard(card)
    }

    companion object {
        private const val OPTIONS = 3
    }
}
