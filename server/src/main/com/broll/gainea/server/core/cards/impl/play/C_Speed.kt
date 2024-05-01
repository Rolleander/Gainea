package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.MOVEMENT
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.GlobalBuff
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.processing.thisTurn

class C_Speed : Card(
    21, MOVEMENT,
    "Friedliche Eroberung",
    "Eure Einheiten können diesen Zug um zwei zusätzliche Felder bewegt werden. Während dieses Zuges könnt ihr nicht mehr angreifen."
) {

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val buffSpeed = IntBuff(BuffType.ADD, 2)
        val buffNoAttaks = IntBuff(BuffType.SET, 0)
        GlobalBuff.createForPlayer(
            game,
            owner,
            buffSpeed,
            { it.movesPerTurn.addBuff(buffSpeed) },
            NT_Event.EFFECT_BUFF
        )
        GlobalBuff.createForPlayer(
            game,
            owner,
            buffNoAttaks,
            { it.attacksPerTurn.addBuff(buffNoAttaks) },
            NT_Event.EFFECT_DEBUFF
        )
        game.buffProcessor.timeoutBuff(buffSpeed, thisTurn())
        game.buffProcessor.timeoutBuff(buffNoAttaks, thisTurn())
    }
}
