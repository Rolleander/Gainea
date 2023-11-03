package com.broll.gainea.server.core.events

import com.broll.gainea.server.core.utils.UnitControl.spawnMonsters

class E_SpawnMonster : EventCard(60, "Rückkehr der Natur", "Ein wildes Monster taucht auf!") {
    override fun play() {
        game.spawnMonsters(1)
    }
}
