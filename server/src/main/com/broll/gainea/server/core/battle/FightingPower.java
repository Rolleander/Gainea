package com.broll.gainea.server.core.battle;


import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.objects.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FightingPower {

    private int diceCount;
    private int lowestNumber = 1;
    private int highestNumber = 6;
    private int numberPlus = 0;

    public FightingPower(Unit unit) {
        this(unit.getPower().getValue());
    }

    public FightingPower(int diceCount) {
        this.diceCount = diceCount;
    }

    public FightingPower changeDiceNumber(int count) {
        diceCount += count;
        if (diceCount < 1) {
            diceCount = 1;
        }
        return this;
    }

    public FightingPower changeHighestNumber(int delta) {
        this.highestNumber += delta;
        return this;
    }

    public FightingPower changeLowestNumber(int delta) {
        this.lowestNumber += delta;
        return this;
    }

    public FightingPower changeNumberPlus(int delta) {
        this.numberPlus += delta;
        return this;
    }

    public FightingPower setDiceCount(int diceCount) {
        this.diceCount = diceCount;
        if (diceCount < 1) {
            diceCount = 1;
        }
        return this;
    }

    public FightingPower withHighestNumber(int highestNumber) {
        this.highestNumber = highestNumber;
        return this;
    }

    public FightingPower withLowestNumber(int lowestNumber) {
        this.lowestNumber = lowestNumber;
        return this;
    }

    public FightingPower setNumberPlus(int numberPlus) {
        this.numberPlus = numberPlus;
        return this;
    }

    public int getDiceCount() {
        return diceCount;
    }

    public List<Integer> roll() {
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < diceCount; i++) {
            rolls.add(rollDice());
        }
        //sort descending
        Collections.sort(rolls, Collections.reverseOrder());
        return rolls;
    }

    private int rollDice() {
        return RandomUtils.random(lowestNumber, highestNumber) + numberPlus;
    }

    public int getHighestNumber() {
        return highestNumber;
    }

    public int getLowestNumber() {
        return lowestNumber;
    }

    public int getNumberPlus() {
        return numberPlus;
    }
}
