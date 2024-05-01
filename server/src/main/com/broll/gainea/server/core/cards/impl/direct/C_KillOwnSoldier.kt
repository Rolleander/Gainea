package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.utils.UnitControl.kill
import com.broll.gainea.server.core.utils.iteratePlayers
import com.broll.gainea.server.core.utils.selectPlayerUnit

class C_KillOwnSoldier : DirectlyPlayedCard(
    24,
    DISRUPTION,
    "Opferritual",
    "Jeder Spieler muss eine eigene Einheit opfern."
) {
    init {
        drawChance = 0.8f
    }

    override fun play() {
        with(game) {
            iteratePlayers(500) { player ->
                val unit = selectPlayerUnit(
                    player,
                    player,
                    "Welche Einheit soll geopfert werden?"
                ) { true }
                if (unit != null) {
                    kill(unit)
                }
            }
        }
    }
}
