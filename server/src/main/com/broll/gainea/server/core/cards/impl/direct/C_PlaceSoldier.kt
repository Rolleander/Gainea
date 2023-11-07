package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.iteratePlayers

class C_PlaceSoldier :
    DirectlyPlayedCard(82, "Ritterdienst", "Jeder Spieler platziert einen Soldat") {
    override fun play() {
        game.iteratePlayers(1000) { player -> player.fraction.placeSoldier() }
    }
}
