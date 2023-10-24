package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_CardPickup : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            val obj = Collectible(game)
            obj.onPickup = {
                it.cardHandler.drawRandomCard()
            }
            obj.name = "Schatztruhe"
            obj.description = "Eroberer zieht eine Karte"
            obj.icon = 131
            game.spawn(obj, area)
        }
    }

}