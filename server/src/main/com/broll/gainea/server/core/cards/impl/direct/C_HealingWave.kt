package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.UnitControl.heal

class C_HealingWave : DirectlyPlayedCard(64, "Kristallenergie", "Heilt alle neutralen Einheiten") {
    override fun play() {
        game.objects.filterIsInstance(Unit::class.java).filter { it.hurt }.forEach { heal(game, it, 10000) }
    }
}
