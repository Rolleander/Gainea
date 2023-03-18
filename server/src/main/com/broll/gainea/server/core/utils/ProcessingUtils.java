package com.broll.gainea.server.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProcessingUtils {
    private final static Logger Log = LoggerFactory.getLogger(ProcessingUtils.class);

    private final static int DEFAULT_PAUSE = 1000;

    public static int MAX_PAUSE = 0;

    public static void pause() {
        pause(DEFAULT_PAUSE);
    }

    public static void pause(int ms) {
        if (MAX_PAUSE > 0) {
            ms = Math.min(MAX_PAUSE, ms);
        }
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                throw new RuntimeException("shutdown");
            }
        }
    }

}
