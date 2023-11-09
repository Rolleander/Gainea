package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeBuildingSpot
import com.broll.gainea.server.core.objects.Building
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl.despawn
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.UnitControl.update

class RE_Portal : RandomEvent() {

    override fun pickSpot() = game.freeBuildingSpot(onlyContinents = true)
    override fun run() {
        game.spawn(Portal(game.neutralPlayer), location)
    }


    private class Portal(owner: Player) : Building(owner) {
        var remainingTeleports = 5

        init {
            name = "Magieportal"
            icon = 7
            updateDescription()
        }

        private fun updateDescription() {
            description =
                "Teleportiert besetzende Einheiten jede Runde auf zufällige Felder." +
                        " Verschwindet nach $remainingTeleports Einsätzen"
        }

        override fun roundStarted() {
            val units = location.units.take(remainingTeleports)
            if (units.isNotEmpty()) {
                units.forEach {
                    game.map.allLocations.filter { it.free }.randomOrNull()?.let { location ->
                        game.move(it, location)
                        remainingTeleports--
                    }
                }
                if (remainingTeleports <= 0) {
                    game.despawn(this)
                } else {
                    game.update(this)
                }
            }
        }
    }
}
