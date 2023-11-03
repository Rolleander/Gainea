package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_StarsPickup : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            val obj = Collectible(game)
            obj.onPickup = {
                it.goalHandler.addStars(9)
            }
            obj.name = "Schatztruhe"
            obj.description = "Eroberer erhält 9 Sterne"
            obj.icon = 131
            game.spawn(obj, area)
        }
    }

}