package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeBuildingSpot
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Market : RandomEvent() {

    override fun pickSpot() = game.freeBuildingSpot()
    override fun run() {
        val obj = Conquerable(game, despawn = false)
        obj.afterConquer = { player ->
            player.cardHandler.drawRandomPlayableCard()
        }
        obj.holdForRounds = 2
        obj.name = "Marktplatz"
        obj.description =
            "Besetzer erhält alle 2 Runden eine Karte"
        obj.icon = 5
        game.spawn(obj, location)
    }

}