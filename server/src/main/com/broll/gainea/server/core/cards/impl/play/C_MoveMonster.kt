package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.SelectionUtils.selectWildMonster
import com.broll.gainea.server.core.utils.UnitControl

class C_MoveMonster : Card(57, "Herdentrieb", "Wählt ein Monster und bewegt es auf ein beliebiges freies Feld") {
    override val isPlayable: Boolean
        get() = game.objects.any { it is Monster }

    override fun play() {
        val monster: Unit = selectWildMonster(game, "Wählt ein Monster das bewegt werden soll")
                ?: return
        UnitControl.move(game, monster, selectHandler.selectLocation("Wählt das Reiseziel", game.map.allAreas.filter { it.free }))
    }
}
