package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeBuildingSpot
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Tower : RandomEvent() {

    override fun pickSpot() = game.freeBuildingSpot()
    override fun run() {
        val obj = Conquerable(game)
        obj.whenRoundsHold = { player ->
            player.goalHandler.addStars(3)
        }
        obj.name = "Turmspitze"
        obj.description =
            "Besetzender Spieler erhält jede Runde 3 Sterne"
        obj.icon = 1
        game.spawn(obj, location)
    }

}