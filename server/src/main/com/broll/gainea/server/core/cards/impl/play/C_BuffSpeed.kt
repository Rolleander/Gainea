package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.utils.selectPlayerUnit

class C_BuffSpeed : Card(28, "Reittier", "Verleiht einer eurer Einheiten eine zusätzliche Bewegungsaktion pro Zug") {
    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty()

    override fun play() {
        val unit = game.selectPlayerUnit(owner, "Wählt eine Einheit")
        if (unit != null) {
            val buff = IntBuff(BuffType.ADD, 1)
            unit.movesPerTurn.addBuff(buff)
        }
    }
}
