package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCardimport

com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.IntBuffimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.utils.UnitControlimport java.util.function.Consumerimport java.util.stream.Collectors
class C_MonsterRevenge : DirectlyPlayedCard(33, "Gesetz des Stärkeren", "Für " + ROUNDS + " Runden erhalten alle wilden Monster +" + BUFF + "/+" + BUFF) {
    init {
        drawChance = 0.6f
    }

    override fun play() {
        val buff = IntBuff(BuffType.ADD, BUFF)
        val monsters = game.objects.stream().filter { it: MapObject? -> it is Monster }.map { it: MapObject? -> it as Monster? }.collect(Collectors.toList())
        monsters.forEach(Consumer { monster: Monster? ->
            monster!!.addHealthBuff(buff)
            monster.power.addBuff(buff)
        })
        UnitControl.update(game, monsters)
        game.buffProcessor.timeoutBuff(buff, ROUNDS)
    }

    companion object {
        private const val ROUNDS = 3
        private const val BUFF = 2
    }
}
