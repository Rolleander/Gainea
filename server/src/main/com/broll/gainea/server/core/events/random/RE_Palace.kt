package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeBuildingSpot
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Palace : RandomEvent() {

    override fun pickSpot() = game.freeBuildingSpot(onlyContinents = true)
    override fun run() {
        val obj = Conquerable(game, despawn = true)
        obj.whenRoundsHold = { player ->
            player.goalHandler.addPoints(1)
        }
        obj.holdForRounds = 3
        obj.name = "Schloss " + location.container.name
        obj.description =
            "Verleiht 1 Siegpunkt an den für drei Runden besitzenden Spieler"
        obj.icon = 0
        game.spawn(obj, location)
    }

}