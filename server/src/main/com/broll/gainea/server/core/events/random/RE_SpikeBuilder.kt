package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeContinentArea
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.impl.SpikeTrap
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_SpikeBuilder : RandomEvent() {
    override fun run(game: Game) {

        game.freeContinentArea { area ->
            val builder = object : Soldier(game.neutralPlayer) {
                override fun roundStarted() {
                    location.connectedLocations.filter { it !is Ship && it.free && !it.hasTrap() }
                        .randomOrNull()
                        ?.let {
                            game.move(this, it)
                            if (alive && !it.hasTrap()) {
                                game.spawn(SpikeTrap(game, 3), it)
                            }
                        }
                }
            }
            builder.description = "Baut jede Runde eine Falle"
            builder.setStats(1, 2)
            builder.icon = 95
            builder.name = "Grubenschleicher"
            game.spawn(builder, area)
            game.spawn(SpikeTrap(game, 3), area)
        }
    }


    fun Location.hasTrap() = inhabitants.any { it is SpikeTrap }
}