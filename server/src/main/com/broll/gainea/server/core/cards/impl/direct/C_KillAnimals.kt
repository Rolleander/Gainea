package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.CHAOS
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl.kill

class C_KillAnimals : DirectlyPlayedCard(
    5,
    CHAOS,
    "Trockenzeit",
    COUNT.toString() + " schwache Monster sterben aus"
) {
    override fun play() {
        game.objects.toList().filterIsInstance(Monster::class.java).filter { it !is GodDragon }
            .sortedBy { it.stars }.take(COUNT)
            .forEach { game.kill(it) }
    }

    companion object {
        private const val COUNT = 3
    }
}
