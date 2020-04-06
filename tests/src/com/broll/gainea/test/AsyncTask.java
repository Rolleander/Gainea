package com.broll.gainea.test;

import com.google.common.util.concurrent.SettableFuture;

public class AsyncTask {

    public static <T> T doAsync(IAsyncTask<T> task) {
        SettableFuture<T> future = SettableFuture.create();
        task.run(t -> future.set(t));
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
