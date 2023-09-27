package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.stream.Collectors
class C_TeleportGate : Card(56, "Teleporter", "Teleportiert eine eurer Armeen auf ein beliebiges freies Land der gleichen Karte") {
    init {
        drawChance = 0.3f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler!!.selectLocation("Welche Armee soll teleportiert werden?", owner.controlledLocations)
        val locations = from.container.expansion.allAreas.stream().filter { obj: Area? -> obj!!.isFree }.collect(Collectors.toList())
        val target = selectHandler!!.selectLocation("Wohin?", locations)
        UnitControl.move(game!!, PlayerUtils.getUnits(owner, from), target)
    }
}
