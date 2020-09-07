package com.broll.gainea.server.core.utils;

import com.esotericsoftware.minlog.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProcessingBlock {

    private CompletableFuture future;

    public ProcessingBlock() {
    }

    public void waitFor() {
        try {
            future = new CompletableFuture();
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.error("Failed waiting for processing block", e);
        }
    }

    public void resume() {
        future.complete(null);
    }

}
