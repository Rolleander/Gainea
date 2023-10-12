package com.broll.gainea.server.core.objects.buffs

import com.broll.gainea.server.core.Game


abstract class Buff<V> {
    var turnsActive = 0

    private val buffedValues = mutableSetOf<BuffableValue<*, V, Buff<V>>>()
    fun register(buffableValue: BuffableValue<*, V, Buff<V>>) {
        buffedValues.add(buffableValue)
    }

    val affectedObjects: List<Any>
        get() = buffedValues.map { it.target!! }


    fun remove() = buffedValues.forEach { it.clearBuff(this) }

}

fun Buff<*>.roundsActive(game: Game) = turnsActive / game.allPlayers.size