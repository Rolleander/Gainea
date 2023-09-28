package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.GameUtils
import com.broll.gainea.server.core.utils.UnitControl.damage

class C_Thunder : DirectlyPlayedCard(50, "Donnerschauer", "Verursacht 1 Schaden an " + COUNT + " zufälligen Einheiten im Spiel") {
    override fun play() {
        GameUtils.getAllUnits(game).shuffled().take(COUNT).forEach { damage(game, it) }
    }

    companion object {
        private const val COUNT = 7
    }
}
