package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.getEnemyLocations
import java.util.stream.Collectors

class C_MoveEnemy : Card(19, "Überläufer", "Versetzt eine feindliche Truppe auf ein beliebiges freies Feld der gleichen Landmasse.") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val location = selectHandler.selectLocation("Armee wählen", game.getEnemyLocations(owner).toList())
        val units = location.inhabitants.stream().collect(Collectors.toList())
        val targets = location.container.areas.filter { it.free }
        if (targets.isNotEmpty()) {
            val target = selectHandler.selectLocation("Zielort wählen", targets)
            game.move(units, target)
        }
    }
}
