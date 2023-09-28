package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.SelectionUtils.selectPlayerUnit
import com.broll.gainea.server.core.utils.UnitControl

class C_BuffMighty : Card(10, "Excaliburs Macht", "Verdoppelt Angriff und Leben einer Einheit") {
    init {
        drawChance = 0.2f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit = selectPlayerUnit(game, owner, "Welche Einheit soll gestärkt werden?")
        if (unit != null) {
            unit.setPower(unit.power.rootValue * 2)
            unit.changeHealth(unit.health.rootValue * 2)
            UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF)
        }
    }
}
