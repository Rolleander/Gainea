package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.map.Shipimport com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.stream.Collectors
class C_Tunnel : Card(77, "Tunnelgräber", "Bewegt eine Truppe auf ein beliebiges freies Gebiet, welches maximal " + DISTANCE + " Felder entfernt ist") {
    override val isPlayable: Boolean
        get() = !owner.units.isEmpty()

    override fun play() {
        val from = selectHandler!!.selectLocation("Wählt eine Truppe", owner.controlledLocations)
        val units = PlayerUtils.getUnits(owner, from)
        val targets = LocationUtils.getConnectedLocations(from, DISTANCE).stream()
                .filter { it: Location? -> it!!.isFree && it !is Ship }.collect(Collectors.toList())
        val to = selectHandler!!.selectLocation("Wählt das Reiseziel", targets)
        UnitControl.move(game!!, units, to)
    }

    companion object {
        private const val DISTANCE = 4
    }
}
