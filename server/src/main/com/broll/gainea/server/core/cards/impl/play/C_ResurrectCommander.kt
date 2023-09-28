package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.PlayerUtils

class C_ResurrectCommander : Card(61, "Auferstehung", "Lasst euren gefallenen Feldherr zurückkehren") {
    init {
        drawChance = 2f
    }

    override val isPlayable: Boolean
        get() = !PlayerUtils.isCommanderAlive(owner)

    override fun play() {
        val locations = owner.controlledLocations.toMutableList()
        if (locations.isEmpty()) {
            locations.add(LocationUtils.getRandomFree(game.map.allAreas))
        }
        placeUnitHandler.placeCommander(owner, locations)
    }
}
