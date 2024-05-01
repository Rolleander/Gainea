package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.SUMMON
import com.broll.gainea.server.core.objects.impl.Challenger
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getRandomFree

class C_UnknownSoldier : DirectlyPlayedCard(
    67,
    SUMMON,
    "Mytseriöser Herausforderer",
    "Ein fremder Herausforderer taucht auf! Wer ihn besiegt erhält einen Siegespunkt."
) {
    init {
        drawChance = 0.2f
    }

    override fun play() {
        val location = game.map.allAreas.getRandomFree()
        if (location != null) {
            val soldier = Challenger(game.neutralPlayer)
            game.spawn(soldier, location)
        }
    }


}

