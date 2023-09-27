package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCardimport

com.broll.gainea.server.core.objects.Soldier com.broll.gainea.server.core.player.Player
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

class C_EliteSoldier : DirectlyPlayedCard(25, "Profi anheuern", "Rekrutiert einen Elitekrieger") {
    override fun play() {
        val soldier = EliteSoldier(owner)
        placeUnitHandler!!.placeUnit(owner, soldier, owner.controlledLocations, "Platziere Elitekrieger")
    }

    private inner class EliteSoldier(owner: Player?) : Soldier(owner) {
        init {
            setStats(2, 2)
            name = "Elitekrieger"
            icon = 105
        }
    }
}
