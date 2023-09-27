package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.cards.DirectlyPlayedCardimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.GlobalBuffimport com.broll.gainea.server.core.objects.buffs.IntBuffimport java.util.function.Consumer
class C_NoAttacks : DirectlyPlayedCard(26, "Nachtruhe", "Für " + ROUNDS + " Runde kann niemand angreifen") {
    override fun play() {
        val buff = IntBuff(BuffType.SET, 0)
        GlobalBuff.Companion.createForAllPlayers(game!!, buff, Consumer<Unit?> { unit: Unit? -> unit.getAttacksPerTurn().addBuff(buff) }, NT_Event.EFFECT_DEBUFF)
        game.buffProcessor.timeoutBuff(buff, ROUNDS)
    }

    companion object {
        private const val ROUNDS = 1
    }
}
