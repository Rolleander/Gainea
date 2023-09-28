package com.broll.gainea.server.core.cards

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl

abstract class TeleportCard(picture: Int, title: String, text: String) : Card(picture, title, text) {
    abstract fun getTeleportTargets(from: Location): List<Location>
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler.selectLocation("Wählt eine Truppe die bewegt werden soll", owner.controlledLocations)
        //filter target locations to empty or locations controlled by the player (cant teleport into enemy location)
        val targets = getTeleportTargets(from).filter { LocationUtils.emptyOrControlled(it, owner) }
        if (targets.isNotEmpty()) {
            val to = selectHandler.selectLocation("Wählt ein Reiseziel", targets)
            UnitControl.move(game, PlayerUtils.getUnits(owner, from), to)
        }
    }
}
