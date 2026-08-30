package com.broll.gainea.server.core.objects

import com.broll.gainea.net.proto.NT_BoardObject
import com.broll.gainea.net.proto.NT_Monster
import com.broll.gainea.net.proto.NT_Unit
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
            return to.goesFrom(location)
        }
        if (location is Ship) {
            return (location as Ship).goesTo(to)
        }
        return true
    }

    val moveTargets: List<Location>
        get() = location.connectedLocations.filter { canMoveTo(it) }

    open fun boardNt(): NT_BoardObject {
        return fillObject(NT_BoardObject.newBuilder()).build()
    }

    protected fun fillObject(obj: NT_BoardObject.Builder) = obj.apply {
        id = this@MapObject.id
        this@MapObject.name?.let { name = it }
        icon = this@MapObject.icon
        scale = this@MapObject.scale
        this@MapObject.description?.let { description = it }
        if (!this@MapObject.owner.isNeutral()) owner = this@MapObject.owner.serverPlayer.id
        if (this@MapObject.location != SpawnLocation) location = this@MapObject.location.number
    }

    protected fun fillObject(obj: NT_Unit.Builder) = obj.apply {
        id = this@MapObject.id
        this@MapObject.name?.let { name = it }
        icon = this@MapObject.icon
        scale = this@MapObject.scale
        this@MapObject.description?.let { description = it }
        if (!this@MapObject.owner.isNeutral()) owner = this@MapObject.owner.serverPlayer.id
        if (this@MapObject.location != SpawnLocation) location = this@MapObject.location.number
    }

    protected fun fillObject(obj: NT_Monster.Builder) = obj.apply {
        id = this@MapObject.id
        this@MapObject.name?.let { name = it }
        icon = this@MapObject.icon
        scale = this@MapObject.scale
        this@MapObject.description?.let { description = it }
        if (!this@MapObject.owner.isNeutral()) owner = this@MapObject.owner.serverPlayer.id
        if (this@MapObject.location != SpawnLocation) location = this@MapObject.location.number
    }

    override fun equals(other: Any?) = other is MapObject && other.id == id
     

    override fun toString(): String {
        return "MapObject{" +
                "id=" + id +
                ", name='" + name +
                ", location=" + location + '\'' +
                '}'
    }
}
