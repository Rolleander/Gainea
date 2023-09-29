package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_Tunnel : Card(77, "Tunnelgräber", "Bewegt eine Truppe auf ein beliebiges freies Gebiet, welches maximal " + DISTANCE + " Felder entfernt ist") {
    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty()

    override fun play() {
        val from = selectHandler.selectLocation("Wählt eine Truppe", owner.controlledLocations)
        val units = PlayerUtils.getUnits(owner, from)
        val targets = LocationUtils.getConnectedLocations(from, DISTANCE)
                .filter { it.free && it !is Ship }
        val to = selectHandler.selectLocation("Wählt das Reiseziel", targets)
        UnitControl.move(game, units, to)
    }

    companion object {
        private const val DISTANCE = 4
    }
}
