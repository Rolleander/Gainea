package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.utils.UnitControlimport java.util.stream.Collectors
class C_RescueFlight : Card(73, "Luftrettung", "Bewegt eine eurer Einheiten auf ein beliebiges freies Feld") {
    init {
        drawChance = 0.3f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit: Unit = selectPlayerUnit(game, owner, "Welche Einheit soll bewegt werden?")
        val target = selectHandler!!.selectLocation("Wohin bewegen?", game.map.allLocations.stream().filter { obj: Location? -> obj!!.isFree }.collect(Collectors.toList()))
        UnitControl.move(game!!, unit, target)
    }
}
