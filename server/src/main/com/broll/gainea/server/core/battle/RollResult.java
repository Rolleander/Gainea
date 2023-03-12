package com.broll.gainea.server.core.battle;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.objects.Unit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RollResult {

    private List<Roll> rolls;

    public RollResult(BattleContext context, List<Unit> units) {
        this.rolls = units.stream().flatMap(unit -> unit.calcFightingPower(context).roll()
                .stream().map(value -> new Roll(unit, value))).collect(Collectors.toList());
        sort();
    }

    public void plusNumber(int number) {
        this.rolls.forEach(it -> it.value += number);
    }

    public void plusNumber(int number, int affectDiceCount) {
        Collections.shuffle(this.rolls);
        for (int i = 0; i < Math.min(affectDiceCount, rolls.size()); i++) {
            rolls.get(i).value += number;
        }
        sort();
    }

    public void max(int number) {
        this.rolls.forEach(it -> it.value = Math.max(number, it.value));
    }

    public void min(int number) {
        this.rolls.forEach(it -> it.value = Math.min(number, it.value));
    }

    public void addDice(int number) {
        this.rolls.add(new Roll(null, number));
        sort();
    }

    public void addDice() {
        addDice(RandomUtils.random(1, 6));
    }

    public void removeDice(int count) {
        for (int i = 0; i < count; i++) {
            if (rolls.size() > 1) {
                rolls.remove(RandomUtils.random(0, rolls.size() - 1));
            }
        }
        sort();
    }

    public void removeHighestDice(int count) {
        for (int i = 0; i < count; i++) {
            if (rolls.size() > 1) {
                rolls.remove(0);
            }
        }
    }

    public void removeLowestDice(int count) {
        for (int i = 0; i < count; i++) {
            if (rolls.size() > 1) {
                rolls.remove(rolls.size() - 1);
            }
        }
    }

    public int count() {
        return rolls.size();
    }

    public void sort() {
        Collections.sort(rolls, Collections.reverseOrder());
    }

    public List<Roll> getRolls() {
        return this.rolls;
    }

    public class Roll implements Comparable<Roll> {
        public Unit source;
        public int value;

        private Roll(Unit source, int value) {
            this.source = source;
            this.value = value;
        }

        public int compareTo(Roll other) {
            return Integer.compare(this.value, other.value);
        }

    }

}
