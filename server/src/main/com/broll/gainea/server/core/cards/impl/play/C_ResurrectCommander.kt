package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.PlayerUtils
class C_ResurrectCommander : Card(61, "Auferstehung", "Lasst euren gefallenen Feldherr zurückkehren") {
    init {
        drawChance = 2f
    }

    override val isPlayable: Boolean
        get() = !PlayerUtils.isCommanderAlive(owner)

    override fun play() {
        val locations: MutableList<Location?> = ArrayList(owner.controlledLocations)
        if (locations.isEmpty()) {
            locations.add(LocationUtils.getRandomFree(game.map.allAreas))
        }
        placeUnitHandler!!.placeCommander(owner!!, locations)
    }
}
