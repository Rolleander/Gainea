package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.monster.GodDragonimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.utils.SelectionUtilsimport com.broll.gainea.server.core.utils.UnitControl
class C_KillMonster : Card(6, "Rangereinsatz", "Tötet ein beliebiges Monster (Außer Götterdrache)") {
    override val isPlayable: Boolean
        get() = game.objects.stream().anyMatch { it: MapObject? -> it is Monster && it !is GodDragon }

    override fun play() {
        val monster: Unit? = SelectionUtils.selectWildMonster(game!!, "Wählt ein Monster das vernichtet werden soll") { it: Monster? -> it !is GodDragon }
        UnitControl.kill(game!!, monster)
    }
}
