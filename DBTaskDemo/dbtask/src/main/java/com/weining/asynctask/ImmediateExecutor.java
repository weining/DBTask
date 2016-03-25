package com.weining.asynctask;

import java.util.concurrent.Executor;

public class ImmediateExecutor implements Executor {

    private static ImmediateExecutor mInstance = null;

    public static ImmediateExecutor getInstance() {
        if (mInstance == null) {
            synchronized (ImmediateExecutor.class) {
                if (mInstance == null) {
                    mInstance = new ImmediateExecutor();
                }
            }
        }
        return mInstance;
    }


    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }
}
