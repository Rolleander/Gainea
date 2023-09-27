package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.utils.PlayerUtils
class C_Spion : Card(8, "Spion", "Platziert einen Soldat auf ein besetztes Land eines anderen Spielers ohne einen Kampf.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = PlayerUtils.getHostileLocations(game!!, owner)
        placeUnitHandler!!.placeSoldier(owner, ArrayList(locations))
    }
}
