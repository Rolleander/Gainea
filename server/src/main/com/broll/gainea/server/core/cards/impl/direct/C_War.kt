package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl.damage

class C_War : DirectlyPlayedCard(9, "Wilde Schlacht", "Jeder Spieler wählt ein feindlich besetztes Land. Jeder Einheit darauf wird 1 Schaden zugefügt.") {
    init {
        drawChance = 0.5f
    }

    override fun play() {
        PlayerUtils.iteratePlayers(game, 500) { player ->
            val locations = PlayerUtils.getHostileLocations(game, player).toList()
            if (locations.isNotEmpty()) {
                val location = selectHandler.selectLocation(player, "Wähle feindliches Land", locations)
                location.units.forEach { damage(game, it) }
            }
        }
    }
}
