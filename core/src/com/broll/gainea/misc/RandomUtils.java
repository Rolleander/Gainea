package com.broll.gainea.misc;

import com.badlogic.gdx.math.MathUtils;

import java.util.List;

public class RandomUtils {

    public static <T> T pickRandom(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(MathUtils.random(0, list.size() - 1));
    }

}
