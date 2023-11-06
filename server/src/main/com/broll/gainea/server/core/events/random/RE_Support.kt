package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Support : RandomEvent() {

    override fun pickSpot() = game.freeArea()
    override fun run() {
        val obj = Collectible(game)
        obj.onPickup = {
            for (i in 1..3) {
                game.spawn(it.fraction.createSoldier(), obj.location)
            }
        }
        obj.name = "Verstärkung"
        obj.description = "Eroberer erhält 3 Einheiten"
        obj.icon = 130
        game.spawn(obj, location)
    }

}