package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.objects.Soldierimport java.util.stream.Collectors
class C_Vagrant : Card(0, "Einsamer Landstreicher", "Platziert einen neutralen Vagabund (2/2) auf ein beliebiges freies Feld.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val soldier = Soldier(null)
        soldier.icon = 1
        soldier.name = "Vagabund"
        soldier.setStats(2, 2)
        val locations = game.map.allAreas.stream().filter { obj: Area? -> obj!!.isFree }.collect(Collectors.toList())
        val location = selectHandler!!.selectLocation("Standort für Vagabund festlegen", locations)
        spawn(game, soldier, location)
    }
}
