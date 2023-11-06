package com.broll.gainea.server.core.objects

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.despawn
import com.broll.gainea.server.core.utils.UnitControl.update

class Conquerable(game: Game, val despawn: Boolean = true) : MapObject(game.neutralPlayer) {

    var holdForRounds = 1
    lateinit var afterConquer: ((Player) -> kotlin.Unit)

    private var holdingTurns = 0

    init {
        scale = 1.2f
    }

    private fun updateState() {
        val owners = location.units.map { it.owner }.filter { !it.isNeutral() }.distinct()
        if (owners.isEmpty() || owners.size > 1) {
            updateOwner(game.neutralPlayer)
            holdingTurns = 0
            return
        }
        updateOwner(owners.first())
    }

    private fun updateOwner(newOwner: Player) {
        if (owner == newOwner) return
        owner = newOwner
        game.update(this)
    }

    private fun success() {
        holdingTurns = 0
        if (despawn) {
            game.despawn(this)
        }
        afterConquer(owner)
    }

    override fun turnStarted(player: Player) {
        holdingTurns++
        if (!owner.isNeutral() && holdingTurns >= game.activePlayers.size * holdForRounds) {
            success()
        }
    }

    override fun unitsMoved(objects: List<MapObject>, location: Location) {
        updateState()
    }

    override fun unitSpawned(obj: MapObject, location: Location) {
        updateState()
    }

}