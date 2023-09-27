package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.cards.DirectlyPlayedCardimport com.broll.gainea.server.core.map.Shipimport com.broll.gainea.server.core.objects.Soldierimport com.broll.gainea.server.core.utils.LocationUtilsimport java.util.Collectionsimport java.util.stream.Collectors
class C_Pirates : DirectlyPlayedCard(38, "Piraten!", "Piraten befallen zufällige unbesetzte Schiffe") {
    init {
        drawChance = 0.8f
    }

    override fun play() {
        val freeShips = game.map.allShips.stream().filter { obj: Ship? -> LocationUtils.noControlledUnits() }.collect(Collectors.toList())
        Collections.shuffle(freeShips)
        val count = RandomUtils.random(2, 3)
        for (i in 0 until Math.min(count, freeShips.size)) {
            val pirate = Soldier(null)
            pirate.name = "Pirat"
            pirate.icon = 121
            pirate.setPower(2)
            pirate.setHealth(2)
            spawn(game, pirate, freeShips[i])
        }
    }
}
