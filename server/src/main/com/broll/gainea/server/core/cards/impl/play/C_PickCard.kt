package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

java.util.stream.Collectors
class C_PickCard : Card(2, "Arkane Bibliothek", "Wählt zwischen " + OPTIONS + " verschiedenen Aktionskarten aus.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val cards = game.cardStorage.getPlayableCards(OPTIONS)
        val card = cards!![selectHandler!!.selectObject("Wählt eine Karte", cards.stream().map { obj: Card? -> obj!!.nt() }.collect(Collectors.toList()))]
        owner.cardHandler.receiveCard(card)
    }

    companion object {
        private const val OPTIONS = 3
    }
}
