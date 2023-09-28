package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_AttackPower : Card(22, "Sturmangriff", "Verleiht allen Einheiten einer eurer Truppen +1 Angriff für eine Runde") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = owner.controlledLocations
        val location = selectHandler.selectLocation("Wählt eine Truppe", locations)
        val buff = IntBuff(BuffType.ADD, 1)
        val units = PlayerUtils.getUnits(owner, location)
        units.forEach { it.power.addBuff(buff) }
        UnitControl.focus(game, units, NT_Event.EFFECT_BUFF)
        game.buffProcessor.timeoutBuff(buff, 1)
    }
}
