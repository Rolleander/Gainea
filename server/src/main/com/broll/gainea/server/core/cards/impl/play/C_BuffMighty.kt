package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.utils.UnitControl
class C_BuffMighty : Card(10, "Excaliburs Macht", "Verdoppelt Angriff und Leben einer Einheit") {
    init {
        drawChance = 0.2f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit: Unit = selectPlayerUnit(game, owner, "Welche Einheit soll gestärkt werden?")
        unit.setPower(unit.power.rootValue * 2)
        unit.changeHealth(unit.health.rootValue * 2)
        UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF)
    }
}
