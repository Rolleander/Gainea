package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl.kill
import com.broll.gainea.server.core.utils.selectWildMonster

class C_KillMonster :
    Card(6, DISRUPTION, "Rangereinsatz", "Tötet ein beliebiges Monster (Außer Götterdrache)") {
    override val isPlayable: Boolean
        get() = game.objects.any { it is Monster && it !is GodDragon }

    override fun play() {
        game.selectWildMonster("Wählt ein Monster das vernichtet werden soll") { it !is GodDragon }
            ?.let {
                game.kill(it)
            }
    }
}
