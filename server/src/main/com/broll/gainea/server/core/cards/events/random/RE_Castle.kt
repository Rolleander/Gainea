package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Castle : RandomEvent() {
    override fun run(game: Game) {
        game.freeContinentArea { area ->
            val obj = Conquerable(game)
            obj.afterConquer = { player ->
                player.goalHandler.addPoints(1)
            }
            obj.holdForRounds = 3
            obj.name = "Schloss " + area.container.name
            obj.description = "Verleiht 1 Siegpunkt an den für drei Runden alleinig besetzenden Spieler"
            obj.icon = 133
            game.spawn(obj, area)
        }
    }

}