package com.example.matisse.executor;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {
    private static ThreadPoolExecutor threadPoolExecutor;


    public static void newInstance() {
        int size = 4;
        if (threadPoolExecutor == null)
            threadPoolExecutor = new ThreadPoolExecutor(size, size, 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
    }

    public static void newInstance(int size) {
        if (threadPoolExecutor == null)
            threadPoolExecutor = new ThreadPoolExecutor(size, size, 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
    }

    public static void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }
}
