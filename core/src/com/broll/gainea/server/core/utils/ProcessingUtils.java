package com.broll.gainea.server.core.utils;

import com.broll.networklib.server.impl.ConnectionSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProcessingUtils {
    private final static Logger Log = LoggerFactory.getLogger(ProcessingUtils.class);

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
