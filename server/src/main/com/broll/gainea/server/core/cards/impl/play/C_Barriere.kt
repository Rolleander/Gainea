package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.GlobalBuffimport com.broll.gainea.server.core.objects.buffs.IntBuffimport java.util.function.Consumer
class C_Barriere : Card(41, "Magische Barriere", "Eure Einheiten erhalten +" + BUFF + " Leben für " + DURATION + " Runden, können solange aber nicht mehr angreifen.") {
    init {
        drawChance = 0.4f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val healthBuff = IntBuff(BuffType.ADD, BUFF)
        val noAttackBuff = IntBuff(BuffType.SET, 0)
        GlobalBuff.Companion.createForPlayer(game!!, owner, healthBuff, Consumer<Unit?> { unit: Unit? -> unit!!.addHealthBuff(healthBuff) }, NT_Event.EFFECT_BUFF)
        GlobalBuff.Companion.createForPlayer(game!!, owner, noAttackBuff, Consumer<Unit?> { unit: Unit? -> unit.getAttacksPerTurn().addBuff(noAttackBuff) }, NT_Event.EFFECT_DEBUFF)
        game.buffProcessor.timeoutBuff(healthBuff, DURATION)
        game.buffProcessor.timeoutBuff(noAttackBuff, DURATION)
    }

    companion object {
        private const val DURATION = 3
        private const val BUFF = 2
    }
}
