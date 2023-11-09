package com.broll.gainea.server.core.objects

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.despawn
import com.broll.gainea.server.core.utils.UnitControl.update

open class Conquerable(game: Game, val despawn: Boolean = false) : Building(game.neutralPlayer) {

    var holdForRounds = 1
    var whenRoundsHold: ((Player) -> kotlin.Unit)? = null

    private var holdingTurns = 0

    private fun updateState() {
        val owners = location.units.map { it.owner }.filter { !it.isNeutral() }.distinct()
        if (owners.isEmpty() || owners.size > 1) {
            updateOwner(game.neutralPlayer)
        } else {
            updateOwner(owners.first())
        }
    }

    private fun updateOwner(newOwner: Player) {
        if (owner == newOwner) return
        holdingTurns = 0
        owner = newOwner
        game.update(this)
    }

    private fun success() {
        holdingTurns = 0
        if (despawn) {
            game.despawn(this)
        }
        whenRoundsHold?.let { it(owner) }
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

    override fun unitKilled(unit: Unit, throughBattle: BattleResult?) {
        updateState()
    }

}