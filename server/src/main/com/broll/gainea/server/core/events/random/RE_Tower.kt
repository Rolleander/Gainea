package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeContinentArea
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Tower : RandomEvent() {
    override fun run(game: Game) {
        game.freeContinentArea { area ->
            val obj = Conquerable(game, despawn = false)
            obj.afterConquer = { player ->
                player.goalHandler.addStars(3)
            }
            obj.name = "Turmspitze"
            obj.description =
                "Besetzender Spieler erhält jede Runde 3 Sterne"
            obj.icon = 139
            game.spawn(obj, area)
        }
    }

}