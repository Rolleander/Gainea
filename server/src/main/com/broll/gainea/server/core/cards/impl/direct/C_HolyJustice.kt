package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.OTHER
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getRandomFree
import com.broll.gainea.server.core.utils.iteratePlayers

class C_HolyJustice : DirectlyPlayedCard(
    7,
    OTHER,
    "Göttliche Gerechtigkeit",
    "Spieler mit weniger Einheiten erhalten zusätzliche Soldaten"
) {
    init {
        drawChance = 0.6f
    }

    override fun play() {
        val avg = Math.ceil((game.activePlayers.sumOf { it.units.size }
            .toFloat() / game.activePlayers.size).toDouble()).toInt()
        game.iteratePlayers(0) { player: Player ->
            val below = avg - player.units.size
            for (i in 0 until below) {
                val location = player.controlledLocations.randomOrNull()
                    ?: game.map.allAreas.getRandomFree()
                if (location != null) {
                    game.spawn(player.fraction.createSoldier(), location)
                }
            }
        }
    }
}
