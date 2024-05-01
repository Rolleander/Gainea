package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.BUFF
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.processing.thisRound
import com.broll.gainea.server.core.utils.UnitControl.focus
import com.broll.gainea.server.core.utils.getUnits

class C_AttackPower : Card(
    22,
    BUFF,
    "Sturmangriff",
    "Verleiht allen Einheiten einer eurer Truppen +1 Angriff für eine Runde"
) {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = owner.controlledLocations.toList()
        val location = selectHandler.selectLocation("Wählt eine Truppe", locations)
        val buff = IntBuff(BuffType.ADD, 1)
        val units = owner.getUnits(location)
        units.forEach { it.power.addBuff(buff) }
        game.focus(units, NT_Event.EFFECT_BUFF)
        game.buffProcessor.timeoutBuff(buff, thisRound())
    }
}
