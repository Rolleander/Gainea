package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProcessingBlock {

    private final static int INACTIVE_CHECK_INTERVAL = 500;
    private final static Logger Log = LoggerFactory.getLogger(ProcessingBlock.class);

    private CompletableFuture future;

    public ProcessingBlock() {
    }

    public void waitFor(Player player) {
        boolean recheck;
        do {
            recheck = false;
            try {
                future = new CompletableFuture();
                future.get(INACTIVE_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                Log.error("Failed waiting for processing block", e);
            } catch (TimeoutException e) {
                recheck = player.isActive();
            }
        } while (recheck);
    }

    public void resume() {
        future.complete(null);
    }

}
