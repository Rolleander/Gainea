package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.MOVEMENT
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.getEnemyLocations
import com.broll.gainea.server.core.utils.getOtherPlayers

class C_SwapArmies : Card(
    79,
    MOVEMENT,
    "Planwechsel",
    "Tauscht den Standort einer beliebigen eurer Armeen mit der eines anderen Spielers"
) {

    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty() && game.getOtherPlayers(owner)
            .any { it.units.isNotEmpty() }

    override fun play() {
        val from = selectHandler.selectLocation(
            "Welche Armee soll bewegt werden?",
            owner.controlledLocations.toList()
        )
        val to = selectHandler.selectLocation("Wohin bewegen?", game.getEnemyLocations(owner))
        val toUnits = to.units.filter { !it.owner.isNeutral() }
        game.move(from.units.filter { it.owner == owner }, to)
        game.move(toUnits, from)
    }

}
