package com.broll.gainea.server.core.events

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.utils.UnitControl.spawnMonsters

class E_SpawnMonster : EventCard(60, "Rückkehr der Natur", "Neue Monster tauchen auf!") {
    override fun play() {
        val playerCount = game.activePlayers.size
        val min = Math.max(1.0, playerCount * 0.5).toInt()
        val max = (playerCount * 1.5).toInt()
        game.spawnMonsters(RandomUtils.random(min, max))
    }
}
