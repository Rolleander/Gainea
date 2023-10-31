package com.broll.gainea.server.core.objects

import com.broll.gainea.server.core.battle.UnitSnapshot
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.buffs.BuffableInt
import com.broll.gainea.server.core.player.Player

interface IUnit {
    val name: String?
    val id: Int
    val owner: Player
    var maxHealth: BuffableInt<MapObject>
    var power: BuffableInt<MapObject>
    var health: BuffableInt<MapObject>
    var attacksPerTurn: BuffableInt<MapObject>
    var numberPlus: BuffableInt<MapObject>
    var location: Location
    var kills: Int

    val dead: Boolean
        get() = health.value <= 0
    val alive: Boolean
        get() = !dead
}

fun IUnit.resolve() = if (this is UnitSnapshot) source else this as Unit