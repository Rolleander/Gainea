package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.UnitControl.move

class C_SeaStorm : DirectlyPlayedCard(68, "Enterchaos", "Alle Schiffe wechseln ihre Besetzer zufällig mit anderen Schiffen der gleichen Karte") {
    override fun play() {
        game.map.expansions.forEach { expansion ->
            val fullShips = expansion.allShips.filter { !it.free }
            val shipWithUnits = fullShips.map { it.inhabitants }
            fullShips.forEach { it.inhabitants.clear() }
            shipWithUnits.forEach { units ->
                val newShip: Location = RandomUtils.pickRandom(expansion.allShips.filter { it.free })
                game.move(units.toList(), newShip)
            }
        }
    }
}
