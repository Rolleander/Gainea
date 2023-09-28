package com.broll.gainea.server.core.battle

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.objects.Unit
import java.util.Collections

class FightingPower(var diceCount: Int) {
    var lowestNumber = 1
        private set
    var highestNumber = 6
        private set
    var numberPlus = 0
        private set

    constructor(unit: Unit) : this(unit.power.getValue())

    fun changeDiceNumber(count: Int): FightingPower {
        diceCount += count
        if (diceCount < 1) {
            diceCount = 1
        }
        return this
    }

    fun changeHighestNumber(delta: Int): FightingPower {
        highestNumber += delta
        return this
    }

    fun changeLowestNumber(delta: Int): FightingPower {
        lowestNumber += delta
        return this
    }

    fun changeNumberPlus(delta: Int): FightingPower {
        numberPlus += delta
        return this
    }

    fun setDiceCount(diceCount: Int): FightingPower {
        var diceCount = diceCount
        this.diceCount = diceCount
        if (diceCount < 1) {
            diceCount = 1
        }
        return this
    }

    fun withHighestNumber(highestNumber: Int): FightingPower {
        this.highestNumber = highestNumber
        return this
    }

    fun withLowestNumber(lowestNumber: Int): FightingPower {
        this.lowestNumber = lowestNumber
        return this
    }

    fun setNumberPlus(numberPlus: Int): FightingPower {
        this.numberPlus = numberPlus
        return this
    }

    fun roll(): List<Int> {
        val rolls: MutableList<Int> = ArrayList()
        for (i in 0 until diceCount) {
            rolls.add(rollDice())
        }
        //sort descending
        Collections.sort(rolls, Collections.reverseOrder())
        return rolls
    }

    private fun rollDice(): Int {
        return RandomUtils.random(lowestNumber, highestNumber) + numberPlus
    }
}
