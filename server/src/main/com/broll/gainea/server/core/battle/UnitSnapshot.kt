package com.broll.gainea.server.core.battle

import com.broll.gainea.server.core.objects.IUnit
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.buffs.BuffableInt

class UnitSnapshot(val source: Unit) : IUnit {
    override val name = source.name
    override val id = source.id
    override val owner = source.owner
    override var maxHealth: BuffableInt<MapObject> = source.maxHealth.frozenCopy(source)
    override var power: BuffableInt<MapObject> = source.power.frozenCopy(source)
    override var health: BuffableInt<MapObject> = source.health.frozenCopy(source)
    override var attacksPerTurn: BuffableInt<MapObject> = source.attacksPerTurn.frozenCopy(source)
    override var numberPlus: BuffableInt<MapObject> = source.numberPlus.frozenCopy(source)
    override var location = source.location
    override var kills = source.kills
}

