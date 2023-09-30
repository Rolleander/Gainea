package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.getRandomFree
import com.broll.gainea.server.core.utils.isCommanderAlive

class C_ResurrectCommander : Card(61, "Auferstehung", "Lasst euren gefallenen Feldherr zurückkehren") {
    init {
        drawChance = 2f
    }

    override val isPlayable: Boolean
        get() = !owner.isCommanderAlive()

    override fun play() {
        val locations = owner.controlledLocations.toMutableList()
        if (locations.isEmpty()) {
            game.map.allAreas.getRandomFree()?.let {
                locations += it
            }
        }
        placeUnitHandler.placeCommander(owner, locations)
    }
}
