package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getRandomFree
import com.broll.gainea.server.core.utils.isCommanderAlive

class C_ReviveCommanders : DirectlyPlayedCard(55, "Rückkehr der Legenden", "Gefallene Feldherren kehren zu allen Spielern zurück") {
    init {
        drawChance = 0.5f
    }

    override fun play() {
        game.activePlayers.filter { it.isCommanderAlive() }.forEach { player ->
            val location = player.controlledLocations.randomOrNull()
                    ?: game.map.allAreas.getRandomFree()
            if (location != null) {
                game.spawn(player.fraction.createCommander(), location)
            }
        }
    }
}
