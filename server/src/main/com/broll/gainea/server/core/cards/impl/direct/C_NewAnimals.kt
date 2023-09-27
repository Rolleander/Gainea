package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.cards.DirectlyPlayedCardimport com.broll.gainea.server.core.utils.GameUtilsimport com.broll.gainea.server.core.utils.UnitControl
class C_NewAnimals : DirectlyPlayedCard(58, "Fette Jahre", "Neue Monster bevölkern die Welt erneut") {
    override fun play() {
        val totalAtStart = GameUtils.getTotalStartMonsters(game!!)
        val currentMonsters = GameUtils.getCurrentMonsters(game!!)
        val missing = totalAtStart - currentMonsters
        UnitControl.spawnMonsters(game, RandomUtils.random(1, Math.max(missing, 3)))
    }
}
