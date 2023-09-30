package com.broll.gainea.server.core.battle

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.objects.Unit
import java.util.Collections
import java.util.function.Consumer
import java.util.stream.Collectors

class RollResult(context: BattleContext, units: List<Unit>) {
    val rolls: MutableList<Roll>

    init {
        rolls = units.stream().flatMap { unit: Unit ->
            unit.calcFightingPower(context)!!.roll()
                    .stream().map { value: Int -> Roll(unit, value) }
        }.collect(Collectors.toList())
        sort()
    }

    fun plusNumber(number: Int) {
        rolls.forEach(Consumer { it: Roll? -> it!!.value += number })
    }

    fun plusNumber(number: Int, affectDiceCount: Int) {
        Collections.shuffle(rolls)
        for (i in 0 until Math.min(affectDiceCount, rolls.size)) {
            rolls[i]!!.value += number
        }
        sort()
    }

    fun max(number: Int) {
        rolls.forEach(Consumer { it: Roll? -> it!!.value = Math.max(number, it.value) })
    }

    fun min(number: Int) {
        rolls.forEach(Consumer { it: Roll? -> it!!.value = Math.min(number, it.value) })
    }

    @JvmOverloads
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

    fun sort() {
        Collections.sort(rolls, Collections.reverseOrder())
    }

    inner class Roll(var source: Unit?, var value: Int) : Comparable<Roll> {
        override fun compareTo(other: Roll): Int {
            return Integer.compare(value, other.value)
        }
    }
}
