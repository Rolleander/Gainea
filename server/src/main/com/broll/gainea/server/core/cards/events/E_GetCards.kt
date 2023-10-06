package com.broll.gainea.server.core.cards.events

import com.broll.gainea.server.core.cards.EventCard
import com.broll.gainea.server.core.utils.iteratePlayers


class E_GetCards : EventCard(35, "Markttag", "Jeder Spieler erhält eine Karte") {
    override fun play() {
        game.iteratePlayers(500) {
            it.cardHandler.receiveCard(game.cardStorage.getRandomPlayableCard())
        }
    }
}
