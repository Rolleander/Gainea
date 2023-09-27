package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCardimport

com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.StreamUtilsimport java.util.concurrent.atomic.AtomicInteger
class C_KillRandomSoldier : DirectlyPlayedCard(42, "Gemetzel", "Verursacht 1 Schaden and jeder " + COUNT + "ten Einheit jedes Spielers.") {
    init {
        drawChance = 0.4f
    }

    override fun play() {
        PlayerUtils.iteratePlayers(game!!, 500) { player: Player? ->
            val integer = AtomicInteger(0)
            StreamUtils.safeForEach<Unit?>(player.getUnits().stream().filter { it: Unit? -> integer.incrementAndGet() % COUNT == 0 }
            ) { unit: Unit? -> damage(game, unit, 1) }
        }
    }

    companion object {
        private const val COUNT = 3
    }
}
