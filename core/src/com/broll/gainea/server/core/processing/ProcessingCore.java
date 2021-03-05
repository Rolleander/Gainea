package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.GameContainer;
import com.broll.networklib.server.impl.ConnectionSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProcessingCore {

    private final static Logger Log = LoggerFactory.getLogger(ProcessingCore.class);
    private GameContainer game;
    private ScheduledExecutorService executor;
    private ScheduledExecutorService parallalExecutor;
    private ScheduledFuture<?> lastFuture;
    private Runnable finishedProcessingListener;
    private Queue<RunnableWrapper> queue = new ConcurrentLinkedQueue<>();

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
        execute(runnable, 50);
    }

    public synchronized void execute(Runnable runnable, int afterDelay) {
        if (!game.isGameOver()) {
            RunnableWrapper wrapper = new RunnableWrapper();
            wrapper.runnable = runnable;
            wrapper.delay = afterDelay;
            if (isBusy()) {
                //add to queue
                this.queue.add(wrapper);
            } else {
                //execute directly
                execute(wrapper);
            }
        }
    }

    private void execute(RunnableWrapper wrapper) {
        this.lastFuture = executor.schedule(wrapper, wrapper.delay, TimeUnit.MILLISECONDS);
    }

    public synchronized boolean isBusy() {
        if (lastFuture == null) {
            return false;
        }
        return !lastFuture.isDone() || !queue.isEmpty();
    }

    private synchronized void done() {
        RunnableWrapper wrapper = queue.poll();
        if (wrapper != null) {
            //start next one
            execute(wrapper);
        } else {
            //done with all
            Log.trace(this + " is last wrapper, finished processing! ");
            finishedProcessingListener.run();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    private class RunnableWrapper implements Runnable {
        private Runnable runnable;
        private int delay;

        @Override
        public void run() {
            try {
                Log.trace("execute wrapper " + this);
                runnable.run();
            } catch (Exception e) {
                Log.error("Processing exception:", e);
            }
            done();
        }
    }
}
