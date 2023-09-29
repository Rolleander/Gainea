package com.broll.gainea.server.core.objects

import com.broll.gainea.net.NT_BoardObject
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.buffs.BuffableInt
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter

abstract class MapObject(var owner: Player) : GameUpdateReceiverAdapter() {
    protected lateinit var game: GameContainer
    lateinit var location: Location
    var id = 0
        private set
    var icon = 0
    var name: String? = null
    var scale = 1f
    protected var moveCount = 0
    var movesPerTurn = BuffableInt(this, 1) //default 1 move

    fun init(game: GameContainer) {
        id = game.newObjectId()
        this.game = game
    }

    open fun turnStart() {
        moveCount = 0
    }

    open fun hasRemainingMove() = moveCount < movesPerTurn.value


    open fun moved() {
        moveCount++
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
        val `object` = NT_BoardObject()
        fillObject(`object`)
        return `object`
    }

    protected fun fillObject(`object`: NT_BoardObject) {
        `object`.id = id.toShort()
        `object`.name = name
        `object`.icon = icon.toShort()
        `object`.size = scale * 30
        `object`.location = location.number.toShort()
    }

    override fun toString(): String {
        return "MapObject{" +
                "id=" + id +
                ", name='" + name +
                ", location=" + location + '\'' +
                '}'
    }
}
