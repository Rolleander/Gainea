package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.TeleportCard
import com.broll.gainea.server.core.map.AreaType.PLAINS
import com.broll.gainea.server.core.map.Location

class C_TeleportPlains : TeleportCard(43, "Steppentrip", "Bewegt eine Truppe zu einer beliebigen Steppe auf der gleichen Karte") {
    override fun getTeleportTargets(from: Location) = from.container.expansion.allAreas.filter { it.type == PLAINS }

    override fun validFor(game: Game) = game.map.allAreas.any { it.type == PLAINS }
}
