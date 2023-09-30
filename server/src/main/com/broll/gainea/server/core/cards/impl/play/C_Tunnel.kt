package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.getConnectedLocations
import com.broll.gainea.server.core.utils.getUnits

class C_Tunnel : Card(77, "Tunnelgräber", "Bewegt eine Truppe auf ein beliebiges freies Gebiet, welches maximal " + DISTANCE + " Felder entfernt ist") {
    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty()

    override fun play() {
        val from = selectHandler.selectLocation("Wählt eine Truppe", owner.controlledLocations)
        val units = owner.getUnits(from)
        val targets = from.getConnectedLocations(DISTANCE)
                .filter { it.free && it !is Ship }
        val to = selectHandler.selectLocation("Wählt das Reiseziel", targets)
        game.move(units, to)
    }

    companion object {
        private const val DISTANCE = 4
    }
}
