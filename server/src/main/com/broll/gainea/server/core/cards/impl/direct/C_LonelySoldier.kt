package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_LonelySoldier : DirectlyPlayedCard(18, "Expedition ins Ungewisse", "Ihr erhaltet einen Soldaten auf einem zufälligen freien Feld") {
    override fun play() {
        val location = LocationUtils.getRandomFree(game.map.allAreas)
        if (location != null) {
            val soldier = owner.fraction.createSoldier()
            spawn(game, soldier, location)
        }
    }
}
