package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl.conquer
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Rider : RandomEvent() {
    override fun run(game: Game) {
        game.map.allContinents.flatMap { it.areas }.filter { it.free }.randomOrNull()?.let { area ->
            val rider = object : Soldier(game.neutralPlayer) {
                override fun turnStarted(player: Player) {
                    location.connectedLocations.filter { it !is Ship }.randomOrNull()?.let {
                        game.conquer(listOf(this), it)
                    }
                }
            }
            rider.description = "Erobert in jedem Zug ein benachbartes Gebiet"
            rider.setStats(5, 3)
            rider.icon = 24
            rider.name = "Der weisse Reiter"
            game.spawn(rider, area)
        }
    }
}

