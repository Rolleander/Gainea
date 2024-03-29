package com.broll.gainea.server.core.objects.buffs

class BuffableInt<T>(target: T, value: Int) : BuffableValue<T, Int, IntBuff>(target, value) {
    private var minValue = Int.MIN_VALUE
    private var maxValue = Int.MAX_VALUE

    init {
        this.value = value
        recalc()
    }

    fun copy(target: T): BuffableInt<T> {
        val copy = BuffableInt(target, this.rootValue)
        copy.minValue = minValue
        copy.maxValue = maxValue
        copy.buffs = buffs
        copy.recalc()
        return copy
    }

    fun frozenCopy(target: T): BuffableInt<T> = copy(target).also { it.freeze() }

    override fun recalc() {
        buffedValue = rootValue
        for (buff in buffs) {
            val modifier = buff.modifier
            buffedValue = when (buff.type) {
                BuffType.ADD -> buffedValue + modifier
                BuffType.MULTIPLY -> buffedValue * modifier
                BuffType.SET -> modifier
            }
        }
        if (buffedValue < minValue) {
            buffedValue = minValue
        } else if (buffedValue > maxValue) {
            buffedValue = maxValue
        }
    }

    fun setMinValue(minValue: Int) {
        this.minValue = minValue
        recalc()
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        recalc()
    }

    fun addValue(value: Int) {
        this.rootValue += value
        recalc()
    }
}
