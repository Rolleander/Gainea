package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_ShadowKnight : RandomEvent() {
    override fun run(game: Game) {
        game.map.allAreas.filter { it.free && it.connectedLocations.all { n -> n.free } }
                .randomOrNull()?.let { area ->
                    val knight = ShadowKnight(game.neutralPlayer)
                    game.spawn(knight, area)
                }
    }
}

private class ShadowKnight(owner: Player) : Soldier(owner) {
    init {
        name = "Schattenlord"
        icon = 6
        description = "Greift Einheiten von Spielern an, die sich auf benachbarte Felder bewegen"
        setStats(6, 4)
    }

    override fun moved(units: List<MapObject>, location: Location) {
        if (location.connectedLocations.contains(this.location) && !units.contains(this)
                && units.any { !it.owner.isNeutral() }) {
            game.battleHandler.startBattle(listOf(this), location.units.filter { !it.owner.isNeutral() });
        }
    }
}