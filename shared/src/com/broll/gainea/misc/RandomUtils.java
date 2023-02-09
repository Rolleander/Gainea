package com.broll.gainea.misc;

import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static Random random = new Random();

    public static <T> T pickRandom(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(random(0, list.size() - 1));
    }

    static public int random (int end) {
        return random(0, end);
    }

    static public int random (int start, int end) {
        return (int) (start + (random.nextDouble() * (end - start)));
    }

    static public float random (float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    public static boolean randomBoolean(float chance) {
        return random.nextFloat() < chance;
    }
}
