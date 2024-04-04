package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.GlobalBuff
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.processing.rounds

class C_Barriere : Card(
    41,
    "Magische Barriere",
    "Eure Einheiten erhalten +" + BUFF + " Leben für " + DURATION + " Runden, können solange aber nicht mehr angreifen."
) {
    init {
        drawChance = 0.4f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val healthBuff = IntBuff(BuffType.ADD, BUFF)
        val noAttackBuff = IntBuff(BuffType.SET, 0)
        GlobalBuff.createForPlayer(
            game,
            owner,
            healthBuff,
            { it.addHealthBuff(healthBuff) },
            NT_Event.EFFECT_BUFF
        )
        GlobalBuff.createForPlayer(
            game,
            owner,
            noAttackBuff,
            { it.attacksPerTurn.addBuff(noAttackBuff) },
            NT_Event.EFFECT_DEBUFF
        )
        game.buffProcessor.timeoutBuff(healthBuff, rounds(DURATION))
        game.buffProcessor.timeoutBuff(noAttackBuff, rounds(DURATION))
    }

    companion object {
        private const val DURATION = 3
        private const val BUFF = 2
    }
}
