package com.broll.gainea.server.core.battle

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.net.NT_Battle_Roll
import com.broll.gainea.server.core.objects.Unit

class RollResult(context: BattleContext, units: List<Unit>) {
    val rolls: MutableList<Roll>

    init {
        rolls = units.flatMap { unit ->
            val power = unit.calcFightingPower(context)
            power.roll().map { result ->
                Roll(
                        source = unit,
                        value = result,
                        plus = power.numberPlus,
                        min = power.lowestNumber,
                        max = power.highestNumber
                )
            }
        }.toMutableList()
        sort()
    }

    fun plusNumber(number: Int) {
        rolls.forEach { it.value += number }
    }

    fun plusNumber(number: Int, affectDiceCount: Int) {
        rolls.shuffle()
        for (i in 0 until Math.min(affectDiceCount, rolls.size)) {
            rolls[i].value += number
            rolls[i].plus += number
        }
        sort()
    }

    fun max(number: Int) {
        rolls.forEach {
            it.value = Math.max(number, it.value)
            it.max = number
        }
    }

    fun min(number: Int) {
        rolls.forEach {
            it.value = Math.min(number, it.value)
            it.min = number
        }
    }

    fun addDice(number: Int = RandomUtils.random(1, 6)) {
        rolls.add(Roll(null, number))
        sort()
    }

    fun removeDice(count: Int) {
        for (i in 0 until count) {
            if (rolls.size > 1) {
                rolls.removeAt(RandomUtils.random(0, rolls.size - 1))
            }
        }
        sort()
    }

    fun removeHighestDice(count: Int) {
        for (i in 0 until count) {
            if (rolls.size > 1) {
                rolls.removeAt(0)
            }
        }
    }

    fun removeLowestDice(count: Int) {
        for (i in 0 until count) {
            if (rolls.size > 1) {
                rolls.removeAt(rolls.size - 1)
            }
        }
    }

    fun count(): Int {
        return rolls.size
    }

    private fun sort() {
        rolls.sortDescending()
    }

    inner class Roll(var source: Unit?, var value: Int, var plus: Int = 0, var min: Int = 1, var max: Int = 6) : Comparable<Roll> {
        override fun compareTo(other: Roll): Int {
            return Integer.compare(value, other.value)
        }

        fun nt(): NT_Battle_Roll {
            val nt = NT_Battle_Roll()
            nt.sourceUnit = source?.id ?: -1
            nt.number = value
            nt.min = min
            nt.max = max
            nt.plus = plus
            return nt
        }
    }
}
