package com.broll.gainea.server.core.objects.buffs

abstract class BuffableValue<T, V, B : Buff<V>>(val target: T, initValue: V) {

    private var frozen = false
    var value: V
        set(v) {
            if (frozen) return
            rootValue = v
            recalc()
        }
        get() = buffedValue

    var rootValue: V = initValue
    protected var buffedValue: V = initValue
    protected var buffs = mutableSetOf<B>()
    fun addBuff(buff: B) {
        if (frozen) return
        buffs.add(buff)
        buff.register(this as BuffableValue<*, V, Buff<V>>)
        recalc()
    }

    fun clearBuff(buff: B) {
        if (frozen) return
        buffs.remove(buff)
        recalc()
    }

    fun freeze() {
        frozen = true
    }

    protected abstract fun recalc()
    fun clearBuffs() {
        if (frozen) return
        buffs.clear()
    }

    override fun toString() = value.toString()
}
