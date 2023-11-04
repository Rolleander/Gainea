package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.impl.SpikeTrap
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_SpikeTrap : Card(
    87,
    "Fallgrube",
    "Platziert eine Stachelfalle (verursacht 3 Schaden) auf ein beliebiges freies Feld"
) {

    override val isPlayable: Boolean
        get() = game.map.allAreas.any { it.free }

    override fun play() {
        val targets = game.map.allAreas.filter { it.free }
        val location = selectHandler.selectLocation("Wo soll die Falle errichtet werden?", targets)
        game.spawn(SpikeTrap(game, 3), location)
    }

}
