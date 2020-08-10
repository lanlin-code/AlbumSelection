package com.example.matisse.util;

import android.os.Handler;
import android.os.Looper;

public class ThreadAdjust {
    private static Handler handler = new android.os.Handler(Looper.getMainLooper());

    public static void post(Runnable r) {
        handler.post(r);
    }
}
