package com.broll.gainea.server.core.cards.events

import com.broll.gainea.server.core.cards.EventCardimport

com.broll.gainea.server.core.player.Playerimport java.util.function.Consumer
class E_GetCards : EventCard(35, "Markttag", "Jeder Spieler erhält eine Karte") {
    override fun play() {
        game.activePlayers.forEach(Consumer { player: Player? -> player.getCardHandler().receiveCard(game.cardStorage.randomPlayableCard) })
    }
}
