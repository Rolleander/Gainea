package com.broll.gainea.server.core.objects

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.despawn

class Conquerable(game: Game) : MapObject(game.neutralPlayer) {

    var holdForRounds = 1
    lateinit var afterConquer: ((Player) -> kotlin.Unit)

    private var holdingTurns = 0
    private var currentHolder: Player? = null


    private fun updateState() {
        val owners = location.units.map { it.owner }.filter { !it.isNeutral() }.distinct()
        if (owners.isEmpty() || owners.size > 1) {
            holdingTurns = 0
            currentHolder = null
            return
        }
        currentHolder = owners.first()
    }

    private fun success() {
        holdingTurns = 0
        game.despawn(this)
        afterConquer(currentHolder!!)
    }

    override fun turnStarted(player: Player) {
        holdingTurns++
        if (currentHolder != null && holdingTurns >= game.activePlayers.size * holdForRounds) {
            success()
        }
    }

    override fun moved(units: List<MapObject>, location: Location) {
        updateState()
    }

    override fun spawned(obj: MapObject, location: Location) {
        updateState()
    }

}