package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.TeleportCard
import com.broll.gainea.server.core.map.AreaType.BOG
import com.broll.gainea.server.core.map.Location

class C_TeleportSwamp : TeleportCard(45, "Sumpferkundung", "Bewegt eine Truppe zu einem beliebigen Sumpf auf der gleichen Karte") {
    override fun getTeleportTargets(from: Location) = from.container.expansion.allAreas.filter { it.type == BOG }

    override fun validFor(game: Game) = game.map.allAreas.any { it.type == BOG }
}
