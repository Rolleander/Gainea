package com.broll.gainea.test;

public interface IAsyncTask<T> {

    void run(AsyncTaskCallback<T> callback);

}
