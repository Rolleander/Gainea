package com.broll.gainea.server.core.cards.events

import com.broll.gainea.server.core.cards.EventCardimport

com.broll.gainea.server.core.utils.UnitControl
class E_SpawnMonster : EventCard(60, "Rückkehr der Natur", "Ein wildes Monster taucht auf!") {
    override fun play() {
        UnitControl.spawnMonsters(game, 1)
    }
}
