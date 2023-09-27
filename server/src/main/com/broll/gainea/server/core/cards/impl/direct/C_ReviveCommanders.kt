package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.cards.DirectlyPlayedCardimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.PlayerUtils
class C_ReviveCommanders : DirectlyPlayedCard(55, "Rückkehr der Legenden", "Gefallene Feldherren kehren zu allen Spielern zurück") {
    init {
        drawChance = 0.5f
    }

    override fun play() {
        game.activePlayers.stream().filter { it: Player? -> !PlayerUtils.isCommanderAlive(it) }.forEach { player: Player? ->
            val locations = player.getControlledLocations()
            var location = RandomUtils.pickRandom(locations)
            if (location == null) {
                location = LocationUtils.getRandomFree(game.map.allAreas)
            }
            val commander = player.getFraction().createCommander()
            spawn(game, commander, location)
        }
    }
}
