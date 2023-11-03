package com.broll.gainea.server.core.objects

import com.broll.gainea.net.NT_BoardObject
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.map.SpawnLocation
import com.broll.gainea.server.core.objects.buffs.BuffableInt
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter

abstract class MapObject(var owner: Player) : GameUpdateReceiverAdapter() {
    protected lateinit var game: Game
    var location: Location = SpawnLocation
    var id = 0
        private set
    var icon = 0
    var name: String? = null
    var scale = 1f
    var description: String? = null
    protected var moveCount = 0
    var movesPerTurn = BuffableInt(this, 1) //default 1 move

    open fun init(game: Game) {
        id = game.newObjectId()
        this.game = game
    }

    open fun prepareForTurnStart() {
        moveCount = 0
    }

    open fun hasRemainingMove() = moveCount < movesPerTurn.value


    open fun moved(fromPlayerAction: Boolean = false) {
        if (fromPlayerAction) {
            moveCount++
        }
    }

    open fun canMoveTo(to: Location): Boolean {
        if (!to.traversable) {
            return false
        }
        if (to is Ship) {
            if (!to.passable(location)) {
                return false
            }
        }
        if (location is Ship) {
            if ((location as Ship).to !== to) {
                return false
            }
        }
        return true
    }

    val moveTargets: List<Location>
        get() = location.connectedLocations.filter { canMoveTo(it) }

    open fun nt(): NT_BoardObject {
        val obj = NT_BoardObject()
        fillObject(obj)
        return obj
    }

    protected fun fillObject(obj: NT_BoardObject) {
        obj.id = id.toShort()
        obj.name = name
        obj.icon = icon.toShort()
        obj.scale = scale
        obj.description = description
        if (!owner.isNeutral()) {
            obj.owner = owner.serverPlayer.id.toShort()
        }
        if (location != SpawnLocation) {
            obj.location = location.number.toShort()
        }
    }

    override fun toString(): String {
        return "MapObject{" +
                "id=" + id +
                ", name='" + name +
                ", location=" + location + '\'' +
                '}'
    }
}
