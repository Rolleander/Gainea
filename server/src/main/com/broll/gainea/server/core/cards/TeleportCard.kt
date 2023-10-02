package com.broll.gainea.server.core.cards

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.emptyOrControlledBy
import com.broll.gainea.server.core.utils.getUnits

abstract class TeleportCard(picture: Int, title: String, text: String) : Card(picture, title, text) {
    abstract fun getTeleportTargets(from: Location): List<Location>
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler.selectLocation("Wählt eine Truppe die bewegt werden soll", owner.controlledLocations.toList())
        //filter target locations to empty or locations controlled by the player (cant teleport into enemy location)
        val targets = getTeleportTargets(from).filter { it.emptyOrControlledBy(owner) }
        if (targets.isNotEmpty()) {
            val to = selectHandler.selectLocation("Wählt ein Reiseziel", targets)
            game.move(owner.getUnits(from), to)
        }
    }
}
