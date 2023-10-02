package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.getHostileLocations

class C_Spion : Card(8, "Spion", "Platziert einen Soldat auf ein besetztes Land eines anderen Spielers ohne einen Kampf.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = game.getHostileLocations(owner)
        placeUnitHandler.placeSoldier(owner, locations.toList())
    }
}
