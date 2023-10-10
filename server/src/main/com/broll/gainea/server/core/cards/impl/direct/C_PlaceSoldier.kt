package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.iteratePlayers

//todo : anderes bild
class C_PlaceSoldier : DirectlyPlayedCard(7, "Verstärkung", "Jeder Spieler platziert einen Soldat") {
    override fun play() {
        game.iteratePlayers(1000) { player -> placeUnitHandler.placeSoldier(player) }
    }
}
