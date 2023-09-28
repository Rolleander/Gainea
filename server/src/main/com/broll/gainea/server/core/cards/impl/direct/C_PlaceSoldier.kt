package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.PlayerUtils

class C_PlaceSoldier : DirectlyPlayedCard(7, "Verstärkung", "Jeder Spieler platziert einen Soldat") {
    override fun play() {
        PlayerUtils.iteratePlayers(game, 1000) { player -> placeUnitHandler.placeSoldier(player) }
    }
}
