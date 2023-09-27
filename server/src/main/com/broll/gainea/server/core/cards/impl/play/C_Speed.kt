package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.GlobalBuffimport com.broll.gainea.server.core.objects.buffs.IntBuffimport java.util.function.Consumer
class C_Speed : Card(21, "Friedliche Eroberung", "Eure Einheiten können diesen Zug ein zusätzliches Feld bewegt werden. Während dieses Zuges könnt ihr nicht angreifen.") {
    init {
        drawChance = 0.7f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val buffSpeed = IntBuff(BuffType.ADD, 1)
        val buffNoAttaks = IntBuff(BuffType.SET, 0)
        GlobalBuff.Companion.createForPlayer(game!!, owner, buffSpeed, Consumer<Unit?> { unit: Unit? -> unit!!.movesPerTurn!!.addBuff(buffSpeed) }, NT_Event.EFFECT_BUFF)
        GlobalBuff.Companion.createForPlayer(game!!, owner, buffNoAttaks, Consumer<Unit?> { unit: Unit? -> unit.getAttacksPerTurn().addBuff(buffNoAttaks) }, NT_Event.EFFECT_DEBUFF)
        game.buffProcessor.timeoutBuff(buffSpeed, 1)
        game.buffProcessor.timeoutBuff(buffNoAttaks, 1)
    }
}
