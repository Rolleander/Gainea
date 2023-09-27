package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCardimport

com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.SelectionUtilsimport com.broll.gainea.server.core.utils.UnitControl
class C_KillOwnSoldier : DirectlyPlayedCard(24, "Opferritual", "Jeder Spieler muss eine eigene Einheit opfern.") {
    init {
        drawChance = 0.8f
    }

    override fun play() {
        PlayerUtils.iteratePlayers(game!!, 500) { player: Player? ->
            val unit = SelectionUtils.selectPlayerUnit(game!!, player, player, "Welche Einheit soll geopfert werden?") { it: Unit? -> true }
            if (unit != null) {
                UnitControl.kill(game!!, unit)
            }
        }
    }
}
