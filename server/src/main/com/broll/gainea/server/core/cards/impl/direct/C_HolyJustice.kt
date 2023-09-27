package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.cards.DirectlyPlayedCardimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.PlayerUtils
class C_HolyJustice : DirectlyPlayedCard(7, "Göttliche Gerechtigkeit", "Spieler mit weniger Einheiten erhalten zusätzliche Soldaten") {
    init {
        drawChance = 0.6f
    }

    override fun play() {
        val avg = Math.ceil((game.activePlayers.stream().mapToInt { it: Player? -> it.getUnits().size }.sum().toFloat() / game.activePlayers.size).toDouble()).toInt()
        PlayerUtils.iteratePlayers(game!!, 0) { player: Player? ->
            val below = avg - player.getUnits().size
            for (i in 0 until below) {
                var location = RandomUtils.pickRandom(player.getControlledLocations())
                if (location == null) {
                    location = LocationUtils.getRandomFree(game.map.allAreas)
                }
                spawn(game, player.getFraction().createSoldier(), location)
            }
        }
    }
}
