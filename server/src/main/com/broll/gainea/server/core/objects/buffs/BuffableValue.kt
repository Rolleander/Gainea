package com.broll.gainea.server.core.objects.buffs

abstract class BuffableValue<T,V, B : Buff<V>>(val target: T, initValue : V) {
    protected var value: V = initValue
    protected var rootValue: V  = initValue
    protected var buffedValue: V  = initValue
    protected var buffs = mutableSetOf<B>()
    fun addBuff(buff: B) {
        buffs.add(buff)
        buff.register(this)
        recalc()
    }

    fun clearBuff(buff: B) {
        buffs.remove(buff)
        recalc()
    }

    protected abstract fun recalc()
    fun clearBuffs() {
        buffs.clear()
    }

    fun setValue(value: V) {
        rootValue = value
        recalc()
    }

    fun getValue(): V? {
        return buffedValue
    }

    override fun toString(): String {
        return "" + buffedValue
    }
}
