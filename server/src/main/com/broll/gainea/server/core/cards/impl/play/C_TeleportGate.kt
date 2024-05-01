package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.MOVEMENT
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.getUnits

class C_TeleportGate : Card(
    56,
    MOVEMENT,
    "Teleporter",
    "Teleportiert eine eurer Armeen auf ein beliebiges freies Land der gleichen Karte"
) {
    init {
        drawChance = 0.3f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler.selectLocation(
            "Welche Armee soll teleportiert werden?",
            owner.controlledLocations.toList()
        )
        val locations = from.container.expansion.allAreas.filter { it.free }
        val target = selectHandler.selectLocation("Wohin?", locations)
        game.move(owner.getUnits(from), target)
    }
}
