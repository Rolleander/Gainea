package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.SelectionUtils.selectPlayerUnit
import com.broll.gainea.server.core.utils.UnitControl

class C_RescueFlight : Card(73, "Luftrettung", "Bewegt eine eurer Einheiten auf ein beliebiges freies Feld") {
    init {
        drawChance = 0.3f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit = selectPlayerUnit(game, owner, "Welche Einheit soll bewegt werden?") ?: return
        val target = selectHandler.selectLocation("Wohin bewegen?", game.map.allLocations.filter { it.free })
        UnitControl.move(game, unit, target)
    }
}
