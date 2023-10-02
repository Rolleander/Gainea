package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card

class C_PickCard : Card(2, "Arkane Bibliothek", "Wählt zwischen " + OPTIONS + " verschiedenen Aktionskarten aus.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val cards = game.cardStorage.getPlayableCards(OPTIONS)
        val card = cards[selectHandler.selectObject("Wählt eine Karte", cards.map { it.nt() })]
        owner.cardHandler.receiveCard(card)
    }

    companion object {
        private const val OPTIONS = 3
    }
}
