package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_ShadowKnight : RandomEvent() {
    override fun pickSpot() = game.map.allAreas.filter {
        it.free && it.connectedLocations.all { n -> n.free }
                && it.connectedLocations.size >= 2
    }.randomOrNull()

    override fun run() {
        val knight = ShadowKnight(game.neutralPlayer)
        game.spawn(knight, location)
    }

    private class ShadowKnight(owner: Player) : Soldier(owner) {
        init {
            name = "Schattenlord"
            icon = 6
            description =
                "Greift Einheiten von Spielern an, die sich auf benachbarte Felder bewegen"
            setStats(6, 4)
        }

        override fun unitsMoved(units: List<MapObject>, location: Location) {
            if (location.connectedLocations.contains(this.location) && !units.contains(this)
                && units.any { !it.owner.isNeutral() }
            ) {
                game.battleHandler.startBattle(
                    listOf(this),
                    location.units.filter { !it.owner.isNeutral() });
            }
        }
    }
}

