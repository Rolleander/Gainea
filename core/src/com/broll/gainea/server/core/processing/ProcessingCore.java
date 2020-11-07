package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.GameContainer;
import com.esotericsoftware.minlog.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProcessingCore {

    private GameContainer game;
    private ScheduledExecutorService executor;
    private ScheduledExecutorService parallalExecutor;
    private ScheduledFuture<?> lastFuture;
    private RunnableWrapper lastWrapper;
    private Runnable finishedProcessingListener;

    public ProcessingCore(GameContainer game, Runnable finishedProcessingListener) {
        this.game = game;
        this.finishedProcessingListener = finishedProcessingListener;
        this.executor = Executors.newScheduledThreadPool(1);
        this.parallalExecutor = Executors.newScheduledThreadPool(3);
    }

    public void executeParallel(Runnable runnable) {
        executeParallel(runnable, 0);
    }

    public synchronized void executeParallel(Runnable runnable, int afterDelay) {
        parallalExecutor.schedule(runnable, afterDelay, TimeUnit.MILLISECONDS);
    }

    public void execute(Runnable runnable) {
        execute(runnable, 0);
    }

    public synchronized void execute(Runnable runnable, int afterDelay) {
        if (!game.isGameOver()) {
            RunnableWrapper wrapper = new RunnableWrapper();
            wrapper.runnable = runnable;
            this.lastWrapper = wrapper;
            lastFuture = executor.schedule(wrapper, afterDelay, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized boolean isBusy() {
        if (lastFuture == null) {
            return false;
        }
        return !lastFuture.isDone();
    }

    public void shutdown() {
        executor.shutdown();
    }

    private class RunnableWrapper implements Runnable {
        private Runnable runnable;

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                Log.error("Processing exception:", e);
            }
            //check if it was the last runnable
            if (lastWrapper == this) {
                finishedProcessingListener.run();
            }
        }
    }
}