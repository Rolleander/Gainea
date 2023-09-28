package com.broll.gainea.server.core.cards.events

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.cards.EventCard
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.UnitControl

class E_SpawnGoddrake : EventCard(59, "Gaineas Herrscher", "Der Götterdrache erscheint!") {
    override fun play() {
        val dragon = GodDragon()
        val location = LocationUtils.getRandomFree(game.map.allAreas)
        UnitControl.spawn(game, dragon, location) { nt -> nt.sound = "goddrake.ogg" }
    }

    companion object {
        fun isGoddrakeAlive(game: GameContainer): Boolean {
            return game.objects.any { it is GodDragon }
        }
    }
}
