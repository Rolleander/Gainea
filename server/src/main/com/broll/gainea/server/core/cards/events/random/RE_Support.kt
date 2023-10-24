package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Support : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            val obj = Collectible(game)
            obj.onPickup = {
                for (i in 1..3) {
                    game.spawn(it.fraction.createSoldier(), obj.location)
                }
            }
            obj.name = "Verst�rkung"
            obj.description = "Eroberer erh�lt 3 Einheiten"
            obj.icon = 130
            game.spawn(obj, area)
        }
    }

}