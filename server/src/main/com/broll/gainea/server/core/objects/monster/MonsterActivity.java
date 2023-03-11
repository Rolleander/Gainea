package com.broll.gainea.server.core.objects.monster;


import com.broll.gainea.misc.RandomUtils;

public enum MonsterActivity {
    ALWAYS(1, 1), OFTEN(1, 2), SOMETIMES(2, 4), RARELY(4, 6);

    private int min, max;

    MonsterActivity(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getTurnTimer() {
        return RandomUtils.random(min, max);
    }
}
