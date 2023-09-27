package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.GlobalBuffimport com.broll.gainea.server.core.objects.buffs.IntBuffimport java.util.function.Consumer
class C_PlayerNoAttacks : Card(14, "Wachpatrouille", "Wählt einen Gegner, dieser kann " + DURATION + " Runden keine Angriffe ausführen.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val player = selectHandler!!.selectOtherPlayer(owner, "Welcher Spieler darf nicht mehr angreifen?")
        val buff = IntBuff(BuffType.SET, 0)
        GlobalBuff.Companion.createForPlayer(game!!, player, buff, Consumer<Unit?> { unit: Unit? -> unit.getAttacksPerTurn().addBuff(buff) }, NT_Event.EFFECT_DEBUFF)
        game.buffProcessor.timeoutBuff(buff, DURATION)
    }

    companion object {
        private const val DURATION = 3
    }
}
