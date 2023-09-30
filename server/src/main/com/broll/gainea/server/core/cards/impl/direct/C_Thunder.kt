package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.UnitControl.damage
import com.broll.gainea.server.core.utils.getAllUnits

class C_Thunder : DirectlyPlayedCard(50, "Donnerschauer", "Verursacht 1 Schaden an " + COUNT + " zufälligen Einheiten im Spiel") {
    override fun play() {
        game.getAllUnits().shuffled().take(COUNT).forEach { game.damage(it) }
    }

    companion object {
        private const val COUNT = 7
    }
}
