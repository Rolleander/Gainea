package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.TeleportCard
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location

class C_TeleportLake : TeleportCard(47, "Küstenreise", "Bewegt eine Truppe zu einem beliebigem Meer auf der gleichen Karte") {

    override fun getTeleportTargets(from: Location) = from.container.expansion.allAreas.filter { it.type == AreaType.LAKE }

}
