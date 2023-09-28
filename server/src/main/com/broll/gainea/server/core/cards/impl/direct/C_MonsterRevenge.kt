package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl

class C_MonsterRevenge : DirectlyPlayedCard(33, "Gesetz des Stärkeren", "Für " + ROUNDS + " Runden erhalten alle wilden Monster +" + BUFF + "/+" + BUFF) {
    init {
        drawChance = 0.6f
    }

    override fun play() {
        val buff = IntBuff(BuffType.ADD, BUFF)
        val monsters = game.objects.filterIsInstance(Monster::class.java)
        monsters.forEach {
            it.addHealthBuff(buff)
            it.power.addBuff(buff)
        }
        UnitControl.update(game, monsters)
        game.buffProcessor.timeoutBuff(buff, ROUNDS)
    }

    companion object {
        private const val ROUNDS = 3
        private const val BUFF = 2
    }
}
