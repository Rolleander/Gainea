package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.GameUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_NewAnimals : DirectlyPlayedCard(58, "Fette Jahre", "Neue Monster bevölkern die Welt erneut") {
    override fun play() {
        val totalAtStart = GameUtils.getTotalStartMonsters(game)
        val currentMonsters = GameUtils.getCurrentMonsters(game)
        val missing = totalAtStart - currentMonsters
        UnitControl.spawnMonsters(game, RandomUtils.random(1, Math.max(missing, 3)))
    }
}
