package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.getHostileUnits
import com.broll.gainea.server.core.utils.getOtherPlayers
import com.broll.gainea.server.core.utils.getUnits
import com.broll.gainea.server.core.utils.isHostile
import com.broll.gainea.server.core.utils.owner

class C_ForceAttack : Card(
    86,
    "Wahnsinniger Angriff",
    "Wählt eine feindliche Armee, greift damit einen ihrer benachbarten Feinde an."
) {

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
        walkableNeighbours.filter { it.units.any { unit -> owner.isHostile(unit) } }

    override fun play() {
        val from = selectHandler.selectLocation("Wählt eine feindliche Armee", targets)
        val owner = from.units.owner()
        val to = selectHandler.selectLocation("Wählt ein Angriffsziel", from.validNeighbours(owner))
        game.battleHandler.startBattle(
            from.getUnits(owner),
            to.getHostileUnits(owner),
            allowRetreat = false
        )
    }

}
