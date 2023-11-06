package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeContinentArea
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl.conquer
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Rider : RandomEvent() {

    override fun pickSpot() = game.freeContinentArea()
    override fun run() {
        val rider = object : Soldier(game.neutralPlayer) {
            override fun turnStarted(player: Player) {
                location.connectedLocations.filter { it !is Ship }.randomOrNull()?.let {
                    game.conquer(listOf(this), it)
                }
            }
        }
        rider.description = "Erobert jeden Zug ein benachbartes Gebiet"
        rider.setStats(5, 4)
        rider.icon = 24
        rider.name = "Der weisse Reiter"
        game.spawn(rider, location)
    }
}

