package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_Pirates : DirectlyPlayedCard(38, "Piraten!", "Piraten befallen zufällige unbesetzte Schiffe") {
    init {
        drawChance = 0.8f
    }

    override fun play() {
        val freeShips = game.map.allShips.filter { LocationUtils.noPlayerUnits(it) }.shuffled()
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
