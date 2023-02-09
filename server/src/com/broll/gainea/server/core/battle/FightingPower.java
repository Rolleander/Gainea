package com.broll.gainea.server.core.battle;


import com.broll.gainea.misc.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FightingPower {

    private int diceCount;
    private int lowestNumber = 1;
    private int highestNumber = 6;
    private int numberPlus = 0;

    public FightingPower() {

    }

    public void changeDiceNumber(int count) {
        diceCount += count;
        if (diceCount < 1) {
            diceCount = 1;
        }
    }

    public void changeHighestNumber(int delta) {
        this.highestNumber += delta;
    }

    public void changeLowestNumber(int delta) {
        this.lowestNumber += delta;
    }

    public void changeNumberPlus(int delta) {
        this.numberPlus += delta;
    }

    public void setDiceCount(int diceCount) {
        this.diceCount = diceCount;
        if (diceCount < 1) {
            diceCount = 1;
        }
    }

    public void setHighestNumber(int highestNumber) {
        this.highestNumber = highestNumber;
    }

    public void setLowestNumber(int lowestNumber) {
        this.lowestNumber = lowestNumber;
    }

    public void setNumberPlus(int numberPlus) {
        this.numberPlus = numberPlus;
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
