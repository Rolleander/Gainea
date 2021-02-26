package com.broll.gainea.server.core.processing;

import com.broll.networklib.server.impl.ConnectionSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProcessingBlock {
    private final static Logger Log = LoggerFactory.getLogger(ProcessingBlock.class);

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
