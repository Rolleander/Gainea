package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.selectPlayerUnit

class C_RescueFlight : Card(73, "Luftrettung", "Bewegt eine eurer Einheiten auf ein beliebiges freies Feld") {
    init {
        drawChance = 0.3f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit = game.selectPlayerUnit(owner, "Welche Einheit soll bewegt werden?") ?: return
        val target = selectHandler.selectLocation("Wohin bewegen?", game.map.allLocations.filter { it.free })
        game.move(unit, target)
    }
}
