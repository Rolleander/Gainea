package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl.damage
import java.util.concurrent.atomic.AtomicInteger

class C_KillRandomSoldier : DirectlyPlayedCard(42, "Gemetzel", "Verursacht 1 Schaden and jeder " + COUNT + "ten Einheit jedes Spielers.") {
    init {
        drawChance = 0.4f
    }

    override fun play() {
        PlayerUtils.iteratePlayers(game, 500) { player ->
            val integer = AtomicInteger(0)
            player.units.toList().filter { integer.incrementAndGet() % COUNT == 0 }
                    .forEach { damage(game, it, 1) }
        }
    }

    companion object {
        private const val COUNT = 3
    }
}
