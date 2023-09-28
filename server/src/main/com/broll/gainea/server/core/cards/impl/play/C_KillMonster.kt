package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.SelectionUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_KillMonster : Card(6, "Rangereinsatz", "Tötet ein beliebiges Monster (Außer Götterdrache)") {
    override val isPlayable: Boolean
        get() = game.objects.any { it is Monster && it !is GodDragon }

    override fun play() {
        SelectionUtils.selectWildMonster(game, "Wählt ein Monster das vernichtet werden soll") { it: Monster? -> it !is GodDragon }?.let {
            UnitControl.kill(game, it)
        }
    }
}
