package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.utils.UnitControlimport com.google.common.collect.Lists
class C_MonsterSteer : Card(72, "Monsterköder", "Wählt ein Monster und bewegt es um ein Feld weiter, mögliche Ziele werden dabei angegriffen.") {
    override val isPlayable: Boolean
        get() = game.objects.stream().anyMatch { it: MapObject? -> it is Monster }

    override fun play() {
        val monster: Monster = selectWildMonster(game, "Wähle das Monster das bewegt werden soll")
        val target = selectHandler!!.selectLocation("Wähle das Ziel (Einheiten werden angegriffen)",
                ArrayList(monster.location.connectedLocations))
        UnitControl.conquer(game!!, Lists.newArrayList<Unit?>(monster), target)
    }
}
