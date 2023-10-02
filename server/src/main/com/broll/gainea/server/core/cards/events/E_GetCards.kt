package com.broll.gainea.server.core.cards.events

import com.broll.gainea.server.core.cards.EventCard


class E_GetCards : EventCard(35, "Markttag", "Jeder Spieler erhält eine Karte") {
    override fun play() {
        game.activePlayers.forEach { it.cardHandler.receiveCard(game.cardStorage.getRandomPlayableCard()) }
    }
}
