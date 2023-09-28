package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.SelectionUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_KillOwnSoldier : DirectlyPlayedCard(24, "Opferritual", "Jeder Spieler muss eine eigene Einheit opfern.") {
    init {
        drawChance = 0.8f
    }

    override fun play() {
        PlayerUtils.iteratePlayers(game, 500) { player ->
            val unit = SelectionUtils.selectPlayerUnit(game, player, player, "Welche Einheit soll geopfert werden?") { true }
            if (unit != null) {
                UnitControl.kill(game, unit)
            }
        }
    }
}
