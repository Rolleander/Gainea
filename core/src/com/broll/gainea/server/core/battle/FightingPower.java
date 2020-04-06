package com.broll.gainea.server.core.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FightingPower {

    private int diceCount;
    private int lowestNumber=1;
    private int highestNumber=6;
    private int numberPlus=0;

    public FightingPower(){

    }

    public void setDiceCount(int diceCount) {
        this.diceCount = diceCount;
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

    public List<Integer> roll(){
        List<Integer> rolls = new ArrayList<>();
        for(int i=0; i<diceCount; i++){
            rolls.add(rollDice());
        }
        //sort descending
        Collections.sort(rolls,Collections.reverseOrder());
        return rolls;
    }

    private int rollDice(){
        return (int)(Math.random()*(highestNumber-lowestNumber))+lowestNumber+numberPlus;
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
