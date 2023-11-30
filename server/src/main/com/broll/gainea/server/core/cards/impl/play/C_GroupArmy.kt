package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.UnitControl.move

class C_GroupArmy : Card(
    81,
    "Ruhmreiche Parade",
    "Wählt ein beliebiges freies Feld von einer Karte, auf der ihr Einheiten besitzt." +
            " Bewegt alle eure Einheiten der gleichen Karte dorthin."
) {

    init {
        drawChance = 0.5f
    }

    val targets: List<Location>
        get() = owner.controlledLocations.map { it.container }.distinct().flatMap { it.areas }
            .filter { it.free }

    override val isPlayable: Boolean
        get() = targets.isNotEmpty()

    override fun play() {
        val location = selectHandler.selectLocation("Sammelpunkt auswählen",
            owner.controlledLocations.map { it.container.expansion }.distinct()
                .flatMap { it.allAreas }
                .filter { it.free })
        game.move(
            owner.units.filter { it.location.container.expansion == location.container.expansion },
            location
        )
    }
}
