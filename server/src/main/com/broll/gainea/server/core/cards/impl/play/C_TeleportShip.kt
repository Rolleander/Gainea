package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.TeleportCard
import com.broll.gainea.server.core.map.Location

class C_TeleportShip : TeleportCard(31, "Seeleute", "Bewegt eine Truppe zu einem beliebigen Schiff auf der gleichen Karte") {
    override fun getTeleportTargets(from: Location) =
            from.container.expansion.allShips

}
