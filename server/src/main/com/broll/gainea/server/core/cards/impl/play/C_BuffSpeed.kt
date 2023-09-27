package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.IntBuff
class C_BuffSpeed : Card(28, "Reittier", "Verleiht einer eurer Einheiten eine zusätzliche Bewegungsaktion pro Zug") {
    override val isPlayable: Boolean
        get() = !owner.units.isEmpty()

    override fun play() {
        val unit: Unit = selectPlayerUnit(game, owner, "Wählt eine Einheit")
        val buff = IntBuff(BuffType.ADD, 1)
        unit.movesPerTurn!!.addBuff(buff)
    }
}
