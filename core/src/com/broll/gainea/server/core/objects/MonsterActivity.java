package com.broll.gainea.server.core.objects;

import com.badlogic.gdx.math.MathUtils;

public enum MonsterActivity {
    ALWAYS(1, 1), OFTEN(1, 2), SOMETIMES(2, 4), RARELY(4, 6);

    private int min, max;

    MonsterActivity(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getTurnTimer() {
        return MathUtils.random(min, max);
    }
}
