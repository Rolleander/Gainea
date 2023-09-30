package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.UnitControl.damage
import com.broll.gainea.server.core.utils.getHostileLocations
import com.broll.gainea.server.core.utils.iteratePlayers

class C_War : DirectlyPlayedCard(9, "Wilde Schlacht", "Jeder Spieler wählt ein feindlich besetztes Land. Jeder Einheit darauf wird 1 Schaden zugefügt.") {
    init {
        drawChance = 0.5f
    }

    override fun play() {
        game.iteratePlayers(500) { player ->
            val locations = game.getHostileLocations(player).toList()
            if (locations.isNotEmpty()) {
                val location = selectHandler.selectLocation(player, "Wähle feindliches Land", locations)
                location.units.forEach { game.damage(it) }
            }
        }
    }
}
