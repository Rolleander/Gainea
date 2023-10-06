package com.broll.gainea.server.core.cards.events

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.EventCard
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getRandomFree

class E_SpawnGoddrake : EventCard(59, "Gaineas Herrscher", "Der Götterdrache erscheint!") {
    override fun play() {
        val dragon = GodDragon(game.neutralPlayer)
        game.map.allAreas.getRandomFree()?.let {
            game.spawn(dragon, it) { nt -> nt.sound = "goddrake.ogg" }
        }
    }

    companion object {
        fun isGoddrakeAlive(game: Game) = game.objects.any { it is GodDragon }

    }
}
