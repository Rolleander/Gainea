package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.SUMMON
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getRandomFree

class C_LonelySoldier : DirectlyPlayedCard(
    18,
    SUMMON,
    "Expedition ins Ungewisse",
    "Ihr erhaltet einen Soldaten auf einem zufälligen freien Feld"
) {
    override fun play() {
        val location = game.map.allAreas.getRandomFree()
        if (location != null) {
            val soldier = owner.fraction.createSoldier()
            game.spawn(soldier, location)
        }
    }
}
