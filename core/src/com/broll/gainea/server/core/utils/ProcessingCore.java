package com.broll.gainea.server.core.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProcessingCore {

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> lastFuture;
    private RunnableWrapper lastWrapper;
    private Runnable finishedProcessingListener;

    public ProcessingCore(Runnable finishedProcessingListener) {
        this.finishedProcessingListener = finishedProcessingListener;
        this.executor = Executors.newScheduledThreadPool(1);
    }

    public void execute(Runnable runnable) {
        execute(runnable, 0);
    }

    public synchronized void execute(Runnable runnable, int afterDelay) {
        RunnableWrapper wrapper = new RunnableWrapper();
        wrapper.runnable = runnable;
        this.lastWrapper = wrapper;
        lastFuture = executor.schedule(wrapper, afterDelay, TimeUnit.MILLISECONDS);
    }

    public synchronized boolean isBusy() {
        if (lastFuture == null) {
            return false;
        }
        return !lastFuture.isDone();
    }

    private class RunnableWrapper implements Runnable {
        private Runnable runnable;

        @Override
        public void run() {
            runnable.run();
            //check if it was the last runnable
            if (lastWrapper == this) {
                finishedProcessingListener.run();
            }
        }
    }
}
