package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.SUMMON
import com.broll.gainea.server.core.utils.UnitControl.spawnMonsters
import com.broll.gainea.server.core.utils.countNeutralMonsters
import com.broll.gainea.server.core.utils.getTotalStartMonsters

class C_NewAnimals :
    DirectlyPlayedCard(58, SUMMON, "Fette Jahre", "Neue Monster bevölkern die Welt erneut") {
    override fun play() {
        val totalAtStart = game.getTotalStartMonsters()
        val currentMonsters = game.countNeutralMonsters()
        val missing = totalAtStart - currentMonsters
        game.spawnMonsters(RandomUtils.random(1, Math.max(missing, 3)))
    }
}
