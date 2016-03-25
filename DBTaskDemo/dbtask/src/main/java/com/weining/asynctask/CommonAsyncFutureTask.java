package com.weining.asynctask;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

abstract class CommonAsyncFutureTask<V> extends FutureTask<V> {
    private CommonAsyncTask<?, ?, ?> mTask = null;

    public CommonAsyncTask<?, ?, ?> getTask() {
        return mTask;
    }

    public CommonAsyncFutureTask(Callable<V> callable, CommonAsyncTask<?, ?, ?> task) {
        super(callable);
        mTask = task;
    }

    public CommonAsyncFutureTask(Runnable runnable, V result, CommonAsyncTask<?, ?, ?> task) {
        super(runnable, result);
        mTask = task;
    }

    protected abstract void cancelTask();

}
