package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.stream.Collectors
class C_MoveEnemy : Card(19, "Überläufer", "Versetzt eine feindliche Truppe auf ein beliebiges freies Feld der gleichen Landmasse.") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val location = selectHandler!!.selectLocation("Armee wählen", ArrayList(PlayerUtils.getHostileLocations(game!!, owner)))
        val units = location.inhabitants.stream().collect(Collectors.toList())
        val targets = location.container.areas.stream().filter { obj: Area? -> obj!!.isFree }.collect(Collectors.toList())
        if (!targets.isEmpty()) {
            val target = selectHandler!!.selectLocation("Zielort wählen", targets)
            UnitControl.move(game!!, units, target)
        }
    }
}
