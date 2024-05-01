package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.SUMMON
import com.broll.gainea.server.core.utils.getEnemyLocations

class C_Spion : Card(
    8, SUMMON,
    "Spion",
    "Platziert einen Soldat auf ein besetztes Land eines anderen Spielers ohne einen Kampf."
) {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = game.getEnemyLocations(owner)
        placeUnitHandler.placeUnit(owner, owner.fraction.createSoldier(), locations.toList())
    }
}
