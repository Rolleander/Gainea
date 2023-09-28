package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.SelectionUtils.selectWildMonster
import com.broll.gainea.server.core.utils.UnitControl

class C_MonsterSteer : Card(72, "Monsterköder", "Wählt ein Monster und bewegt es um ein Feld weiter, mögliche Ziele werden dabei angegriffen.") {
    override val isPlayable: Boolean
        get() = game.objects.any { it is Monster }

    override fun play() {
        val monster: Monster = selectWildMonster(game, "Wähle das Monster das bewegt werden soll")
                ?: return
        val target = selectHandler.selectLocation("Wähle das Ziel (Einheiten werden angegriffen)",
                ArrayList(monster.location.connectedLocations))
        UnitControl.conquer(game, listOf(monster), target)
    }
}
