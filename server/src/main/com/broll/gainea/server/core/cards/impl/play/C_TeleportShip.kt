package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.TeleportCard

com.broll.gainea.server.core.map.Location
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

class C_TeleportShip : TeleportCard(31, "Seeleute", "Bewegt eine Truppe zu einem beliebigen Schiff auf der gleichen Karte") {
    override fun getTeleportTargets(from: Location?): List<Location?>? {
        return from.getContainer().expansion.allShips
    }
}
