package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCardimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.StreamUtils
class C_War : DirectlyPlayedCard(9, "Wilde Schlacht", "Jeder Spieler wählt ein feindlich besetztes Land. Jeder Einheit darauf wird 1 Schaden zugefügt.") {
    init {
        drawChance = 0.5f
    }

    override fun play() {
        PlayerUtils.iteratePlayers(game!!, 500) { player: Player? ->
            val locations: List<Location?> = ArrayList(PlayerUtils.getHostileLocations(game!!, player))
            if (!locations.isEmpty()) {
                val location = selectHandler!!.selectLocation(player, "Wähle feindliches Land", locations)
                StreamUtils.safeForEach<Unit?>(location.inhabitants.stream().filter { it: MapObject? -> it is Unit }.map<Unit?> { it: MapObject? -> it as Unit? }
                ) { unit: Unit? -> damage(game, unit, 1) }
            }
        }
    }
}
