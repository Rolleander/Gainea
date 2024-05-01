package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.utils.UnitControl.move

class C_Panic : Card(
    46,
    DISRUPTION,
    "Massenpanik",
    "Wählt ein Land mit mindestens einer Einheit. Alle Einheiten des gewählten Ortes werden auf angrenzende Orte verteilt."
) {
    init {
        drawChance = 0.9f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = game.map.allAreas.filter { it.units.isNotEmpty() }
        if (locations.isNotEmpty()) {
            val source =
                selectHandler.selectLocation("Wählt einen Zielort für die Panik", locations)
            val neighbours = source.connectedLocations.toList().shuffled()
            var index = 0
            for (inhabitant in source.units.toList()) {
                val target = neighbours[index]
                game.move(inhabitant, target)
                index++
                if (index >= neighbours.size) {
                    index = 0
                }
            }
        }
    }
}
