package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.UnitControl.focus
import com.broll.gainea.server.core.utils.selectPlayerUnit

class C_BuffMighty : Card(10, "Excaliburs Macht", "Verdoppelt Angriff und Leben einer Einheit") {
    init {
        drawChance = 0.2f
    }

    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty()

    override fun play() {
        val unit = game.selectPlayerUnit(owner, "Welche Einheit soll gestärkt werden?")!!
        unit.setPower(unit.power.rootValue * 2)
        unit.setHealth(unit.health.rootValue * 2)
        game.focus(unit, NT_Event.EFFECT_BUFF)
    }
}
