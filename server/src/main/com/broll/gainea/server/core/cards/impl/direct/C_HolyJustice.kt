package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_HolyJustice : DirectlyPlayedCard(7, "Göttliche Gerechtigkeit", "Spieler mit weniger Einheiten erhalten zusätzliche Soldaten") {
    init {
        drawChance = 0.6f
    }

    override fun play() {
        val avg = Math.ceil((game.activePlayers.sumOf { it.units.size }.toFloat() / game.activePlayers.size).toDouble()).toInt()
        PlayerUtils.iteratePlayers(game, 0) { player: Player ->
            val below = avg - player.units.size
            for (i in 0 until below) {
                var location = player.controlledLocations.randomOrNull()
                if (location == null) {
                    location = LocationUtils.getRandomFree(game.map.allAreas)
                }
                spawn(game, player.fraction.createSoldier(), location)
            }
        }
    }
}
