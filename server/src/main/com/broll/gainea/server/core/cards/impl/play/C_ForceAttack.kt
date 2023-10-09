package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.conquer
import com.broll.gainea.server.core.utils.getOtherPlayers
import com.broll.gainea.server.core.utils.isHostile

class C_ForceAttack : Card(86, "Wahnsinniger Angriff", "Wählt eine feindliche Armee, greift damit einen ihrer benachbarten Feinde an.") {

    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    val targets: List<Location>
        get() = game.getOtherPlayers(owner).flatMap { player ->
            player.controlledLocations.filter { it.validNeighbours(player).isNotEmpty() }
        }

    private fun Location.validNeighbours(owner: Player) =
            connectedLocations.filter { it.units.any { unit -> owner.isHostile(unit) } }

    override fun play() {
        val from = selectHandler.selectLocation("Wählt eine feindliche Armee", targets)
        val enemy = from.units.map { it.owner }.find { !it.isNeutral() }!!
        val to = selectHandler.selectLocation("Wählt ein Angriffsziel", from.validNeighbours(enemy))
        game.conquer(from.units.filter { it.owner == enemy }, to)
    }

}
