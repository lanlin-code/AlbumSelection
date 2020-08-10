package com.example.picasso;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PicassoExecutor extends ThreadPoolExecutor {
    private static int size = 4;

    public PicassoExecutor() {
        super(size, size, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
    }
}
