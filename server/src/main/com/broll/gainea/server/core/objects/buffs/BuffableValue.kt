package com.broll.gainea.server.core.objects.buffs

abstract class BuffableValue<T, V, B : Buff<V>>(val target: T, initValue: V) {
    var value: V
        set(v) {
            rootValue = v
            recalc()
        }
        get() = buffedValue

    var rootValue: V = initValue
    protected var buffedValue: V = initValue
    protected var buffs = mutableSetOf<B>()
    fun addBuff(buff: B) {
        buffs.add(buff)
        buff.register(this as BuffableValue<*, V, Buff<V>>)
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

    override fun toString(): String {
        return "" + buffedValue
    }
}
