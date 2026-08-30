package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.core.protocol.ScreenEffect

import com.broll.gainea.net.proto.NT_Event
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.processing.rounds
import com.broll.gainea.server.core.utils.UnitControl.focus
import com.broll.gainea.server.core.utils.getEnemyLocations

class C_Rooting : Card(
    63, DISRUPTION,
    "Schattenfesseln",
    "Wählt eine feindliche Truppe. Diese kann sich für " + DURATION + " Runden nicht bewegen."
) {
    init {
        drawChance = 0.7f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val location = selectHandler.selectLocation(
            "Ziel für Schattenfesseln wählen",
            game.getEnemyLocations(owner).toList()
        )
        val units = location.inhabitants.toList()
        val rootDebuff = IntBuff(BuffType.SET, 0)
        units.forEach {
            it.movesPerTurn.addBuff(rootDebuff)
            if (it is Unit) {
                it.attacksPerTurn.addBuff(rootDebuff)
            }
        }
        game.focus(units, ScreenEffect.DEBUFF)
        game.buffProcessor.timeoutBuff(rootDebuff, rounds(DURATION))
    }

    companion object {
        private const val DURATION = 2
    }
}
