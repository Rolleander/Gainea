package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_CardPickup : RandomEvent() {

    override fun pickSpot() = game.freeArea()
    override fun run() {
        val obj = Collectible(game)
        obj.onPickup = {
            it.cardHandler.receiveCard(
                game.cardStorage.getRandomPlayableCard()
            )
        }
        obj.name = "Urkunde"
        obj.description = "Eroberer zieht eine Karte"
        obj.icon = 136
        game.spawn(obj, location)
    }

}