package com.broll.gainea.server.core.utils;

import com.esotericsoftware.minlog.Log;

public final class ProcessingUtils {

    private final static int DEFAULT_PAUSE = 1000;

    public static void pause() {
        pause(DEFAULT_PAUSE);
    }

    public static void pause(int ms) {
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                Log.error("Failed to sleep", e);
            }
        }
    }
}
