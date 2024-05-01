package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.GlobalBuff
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.processing.rounds

class C_NoAttacks :
    DirectlyPlayedCard(
        26,
        DISRUPTION,
        "Nachtruhe",
        "Für " + ROUNDS + " Runde kann niemand angreifen"
    ) {
    override fun play() {
        val buff = IntBuff(BuffType.SET, 0)
        GlobalBuff.createForAllPlayers(
            game,
            buff,
            { it.attacksPerTurn.addBuff(buff) },
            NT_Event.EFFECT_DEBUFF
        )
        game.buffProcessor.timeoutBuff(buff, rounds(ROUNDS))
    }

    companion object {
        private const val ROUNDS = 1
    }
}
