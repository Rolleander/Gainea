package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.EffectType.CHAOS
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.utils.UnitControl.move

class C_ShipForward : DirectlyPlayedCard(
    74, CHAOS, "Stürmige See",
    "Alle Schiffe schieben Besetzer um ein Feld in Fahrtrichtung weiter"
) {

    init {
        drawChance = 0.7f
    }

    override fun play() {
        game.map.allShips.map { it.getTargetShip() }.distinct().shuffled().forEach {
            it.moveUnitsForward()
        }
    }

    private fun Ship.moveUnitsForward() {
        game.move(units.toList(), to)
        if (from is Area) {
            game.move(from.units.toList(), this)
        } else {
            (from as Ship).moveUnitsForward()
        }
    }

    private fun Ship.getTargetShip(): Ship =
        if (to is Area) {
            this
        } else {
            (to as Ship).getTargetShip()
        }

}
