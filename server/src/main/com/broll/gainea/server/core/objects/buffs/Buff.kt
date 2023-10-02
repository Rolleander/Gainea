package com.broll.gainea.server.core.objects.buffs

abstract class Buff<V> {
    private val buffedValues = mutableSetOf<BuffableValue<*, V, Buff<V>>>()
    fun register(buffableValue: BuffableValue<*, V, Buff<V>>) {
        buffedValues.add(buffableValue)
    }

    val affectedObjects: List<Any>
        get() = buffedValues.map { it.target!! }

    fun remove() = buffedValues.forEach { it.clearBuff(this) }

}
