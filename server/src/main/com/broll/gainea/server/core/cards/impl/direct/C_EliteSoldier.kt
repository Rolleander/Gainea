package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player

class C_EliteSoldier : DirectlyPlayedCard(25, "Profi anheuern", "Rekrutiert einen Elitekrieger") {
    override fun play() {
        val soldier = EliteSoldier(owner)
        placeUnitHandler.placeUnit(owner, soldier, owner.controlledLocations.toList())
    }

    private inner class EliteSoldier(owner: Player) : Soldier(owner) {
        init {
            setStats(2, 2)
            name = "Elitekrieger"
            icon = 105
        }
    }
}
