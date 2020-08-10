package com.example.picasso.dispatcher;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.example.picasso.Picasso;
import com.example.picasso.action.Action;
import com.example.picasso.cache.Cache;
import com.example.picasso.hunter.BitmapHunter;
import com.example.picasso.util.PicassoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;

public class Dispatcher {
    private static final int ACTION_SUBMIT = 1;
    private static final int ACTION_CANCEL = 2;
    private static final int HUNTER_COMPLETE = 3;
    private static final int HUNTER_BATCH_DELAY = 4;
    private static final int MESSAGE_INTERVAL = 200;
    private static final String TAG = "dispatcher";
    private Map<String, BitmapHunter> hunterMap;
    private ExecutorService service;
    private List<BitmapHunter> batch;
    private Cache cache;
    private Handler handler;
    private Handler mainHandler;
    private HandlerThread dispatchThread;

    public Dispatcher(Handler mainHandler, ExecutorService service, Cache cache) {
        this.mainHandler = mainHandler;
        this.service = service;
        this.cache = cache;
        dispatchThread = new HandlerThread(TAG);
        dispatchThread.start();
        PicassoUtil.flushStack(dispatchThread.getLooper());
        batch = new ArrayList<>();
        hunterMap = new WeakHashMap<>();
        handler = new DispatcherHandler(dispatchThread.getLooper(), this);
    }

    public void shutdown() {
        service.shutdown();
        dispatchThread.quit();
        hunterMap.clear();
    }

    public void dispatchSubmit(Action action) {
        handler.sendMessage(handler.obtainMessage(ACTION_SUBMIT, action));
    }

    public void dispatchCancel(Action action) {
        handler.sendMessage(handler.obtainMessage(ACTION_CANCEL, action));
    }

    public void dispatchComplete(BitmapHunter hunter) {
        handler.sendMessage(handler.obtainMessage(HUNTER_COMPLETE, hunter));
    }

    static class DispatcherHandler extends Handler {
        private Dispatcher dispatcher;

        DispatcherHandler(Looper looper, Dispatcher dispatcher) {
            super(looper);
            this.dispatcher = dispatcher;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_SUBMIT :
                    Action submit = (Action) msg.obj;
                    dispatcher.performSubmit(submit);
                    break;
                case ACTION_CANCEL:
                    Action cancel = (Action) msg.obj;
                    dispatcher.performCancel(cancel);
                    break;
                case HUNTER_COMPLETE:
                    BitmapHunter hunter = (BitmapHunter) msg.obj;
                    dispatcher.performComplete(hunter);
                    break;
                case HUNTER_BATCH_DELAY:
                    dispatcher.performBatchComplete();
                    break;
            }
        }
    }

    private void performComplete(BitmapHunter hunter) {
        cache.put(hunter.getKey(), hunter.getResult());
        hunterMap.remove(hunter.getKey());
        batch(hunter);
    }

    private void performSubmit(Action action) {
        BitmapHunter hunter = hunterMap.get(action.getKey());
        if (hunter != null) return;
        if (service.isShutdown()) return;
        hunter = new BitmapHunter(action, this);
        hunter.future = service.submit(hunter);
        hunterMap.put(hunter.getKey(), hunter);
    }

    private void performCancel(Action action) {
        if (action == null) return;
        BitmapHunter hunter = hunterMap.get(action.getKey());
        if (hunter != null) {
            if (hunter.cancel()) hunterMap.remove(action.getKey());
        }
    }

    private void performBatchComplete() {
        List<BitmapHunter> hunters = new ArrayList<>(batch);
        batch.clear();
        mainHandler.sendMessage(mainHandler.obtainMessage(Picasso.REQUEST_BATCH_COMPLETE, hunters));
    }


    private void batch(BitmapHunter hunter) {
        if (hunter.isCanceled()) return;
        Bitmap result = hunter.getResult();
        if (result != null) {
            result.prepareToDraw();
        }
        batch.add(hunter);
        if (!handler.hasMessages(HUNTER_BATCH_DELAY)) {
            handler.sendMessageDelayed(handler.obtainMessage(HUNTER_BATCH_DELAY), MESSAGE_INTERVAL);
        }
    }
}
