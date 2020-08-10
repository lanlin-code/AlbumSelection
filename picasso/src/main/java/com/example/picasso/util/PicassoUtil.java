package com.example.picasso.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class PicassoUtil {
    public static final int CLEAN_UP_TIME = 1000;
    private static final int MESSAGE_INTERVAL = 200;

    public static void checkMain() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) throw new IllegalStateException();
    }

    public static void flushStack(Looper looper) {
        Handler handler = new Handler(looper) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                sendMessageDelayed(obtainMessage(), MESSAGE_INTERVAL);
            }
        };
        handler.sendMessageDelayed(handler.obtainMessage(), MESSAGE_INTERVAL);
    }
}
