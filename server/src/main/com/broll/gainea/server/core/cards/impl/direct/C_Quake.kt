package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.CHAOS
import com.broll.gainea.server.core.utils.UnitControl.move

class C_Quake : DirectlyPlayedCard(
    88,
    CHAOS,
    "Erdbeben",
    "Jedes Land mit mehr als 3 Einheiten verteilt diese zufällig auf Felder der gleichen Landmasse"
) {

    init {
        drawChance = 1.5f
    }

    override fun play() {
        game.map.allAreas.filter { it.units.size > 3 && it.container.areas.size > 1 }
            .forEach { stackLocation ->
                stackLocation.units.forEach { unit ->
                    game.move(unit, stackLocation.container.areas.shuffled().first())
                }
            }
    }
}
