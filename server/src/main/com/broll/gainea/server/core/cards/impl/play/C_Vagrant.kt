package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_Vagrant : Card(0, "Einsamer Landstreicher", "Platziert einen neutralen Vagabund (2/2) auf ein beliebiges freies Feld.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val soldier = Soldier(game.neutralPlayer)
        soldier.icon = 1
        soldier.name = "Vagabund"
        soldier.setStats(2, 2)
        val locations = game.map.allAreas.filter { it.isFree }
        val location = selectHandler.selectLocation("Standort für Vagabund festlegen", locations)
        spawn(game, soldier, location)
    }
}
