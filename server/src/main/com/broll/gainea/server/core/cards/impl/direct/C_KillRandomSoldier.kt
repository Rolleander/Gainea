package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.CHAOS
import com.broll.gainea.server.core.utils.UnitControl.damage
import com.broll.gainea.server.core.utils.iteratePlayers
import java.util.concurrent.atomic.AtomicInteger

class C_KillRandomSoldier : DirectlyPlayedCard(
    42,
    CHAOS,
    "Gemetzel",
    "Verursacht 1 Schaden and jeder " + COUNT + "ten Einheit jedes Spielers."
) {
    init {
        drawChance = 0.4f
    }

    override fun play() {
        with(game) {
            iteratePlayers(500) { player ->
                val integer = AtomicInteger(0)
                player.units.toList().filter { integer.incrementAndGet() % COUNT == 0 }
                    .forEach { damage(it, 1) }
            }
        }
    }

    companion object {
        private const val COUNT = 3
    }
}
