package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_ReviveCommanders : DirectlyPlayedCard(55, "Rückkehr der Legenden", "Gefallene Feldherren kehren zu allen Spielern zurück") {
    init {
        drawChance = 0.5f
    }

    override fun play() {
        game.activePlayers.filter { !PlayerUtils.isCommanderAlive(it) }.forEach { player ->
            val locations = player.controlledLocations
            var location = RandomUtils.pickRandom(locations)
            if (location == null) {
                location = LocationUtils.getRandomFree(game.map.allAreas)
            }
            spawn(game, player.fraction.createCommander(), location)
        }
    }
}
