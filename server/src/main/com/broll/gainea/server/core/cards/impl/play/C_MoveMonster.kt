package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.utils.UnitControlimport java.util.stream.Collectors
class C_MoveMonster : Card(57, "Herdentrieb", "Wählt ein Monster und bewegt es auf ein beliebiges freies Feld") {
    override val isPlayable: Boolean
        get() = game.objects.stream().anyMatch { it: MapObject? -> it is Monster }

    override fun play() {
        val monster: Unit = selectWildMonster(game, "Wählt ein Monster das bewegt werden soll")
        val locations = game.map.allAreas.stream().filter { obj: Area? -> obj!!.isFree }.collect(Collectors.toList())
        val target = selectHandler!!.selectLocation("Wählt das Reiseziel", locations)
        if (target != null) {
            UnitControl.move(game!!, monster, target)
        }
    }
}
