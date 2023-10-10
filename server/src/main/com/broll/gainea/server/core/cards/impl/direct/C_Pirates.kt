package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.noPlayerUnits

class C_Pirates : DirectlyPlayedCard(38, "Piraten!", "Piraten befallen zufällige unbesetzte Schiffe") {
    init {
        drawChance = 0.8f
    }

    override fun play() {
        val freeShips = game.map.allShips.filter { it.noPlayerUnits() }.shuffled()
        val count = RandomUtils.random(2, 4)
        for (i in 0 until Math.min(count, freeShips.size)) {
            val pirate = Soldier(game.neutralPlayer)
            pirate.name = "Pirat"
            pirate.icon = 121
            pirate.setPower(2)
            pirate.setHealth(2)
            game.spawn(pirate, freeShips[i])
        }
    }
}
