package com.broll.gainea.server.core.objects.buffs

class BuffableBoolean<T>(target: T, value: Boolean) : BuffableValue<T, Boolean, BooleanBuff>(target, value) {
    init {
        this.value = value
        recalc()
    }

    override fun recalc() {
        buffedValue = value
        for (buff in buffs) {
            buffedValue = buff.modifier
        }
    }
}
