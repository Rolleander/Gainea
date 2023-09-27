package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.IntBuffimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.function.Consumer
class C_AttackPower : Card(22, "Sturmangriff", "Verleiht allen Einheiten einer eurer Truppen +1 Angriff für eine Runde") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = owner.controlledLocations
        val location = selectHandler!!.selectLocation("Wählt eine Truppe", locations)
        val buff = IntBuff(BuffType.ADD, 1)
        val units = PlayerUtils.getUnits(owner, location)
        units!!.forEach(Consumer { unit: Unit? -> unit.getPower().addBuff(buff) })
        UnitControl.focus(game, units, NT_Event.EFFECT_BUFF)
        game.buffProcessor.timeoutBuff(buff, 1)
    }
}
