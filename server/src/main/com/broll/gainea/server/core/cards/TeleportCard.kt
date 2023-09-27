package com.broll.gainea.server.core.cards

import com.broll.gainea.server.core.map.Locationimport

com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.stream.Collectors
abstract class TeleportCard(picture: Int, title: String, text: String) : Card(picture, title, text) {
    abstract fun getTeleportTargets(from: Location?): List<Location?>?
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler!!.selectLocation("Wählt eine Truppe die bewegt werden soll", owner.controlledLocations)
        //filter target locations to empty or locations controlled by the player (cant teleport into enemy location)
        val targets = getTeleportTargets(from)!!.stream().filter { it: Location? -> LocationUtils.emptyOrControlled(it, owner!!) }.collect(Collectors.toList())
        if (!targets.isEmpty()) {
            val to = selectHandler!!.selectLocation("Wählt ein Reiseziel", targets)
            UnitControl.move(game!!, PlayerUtils.getUnits(owner, from), to)
        }
    }
}
