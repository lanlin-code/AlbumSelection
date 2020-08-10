package com.example.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.example.picasso.action.Action;
import com.example.picasso.cache.Cache;
import com.example.picasso.dispatcher.Dispatcher;
import com.example.picasso.handler.ContentHandler;
import com.example.picasso.handler.RequestHandler;
import com.example.picasso.hunter.BitmapHunter;
import com.example.picasso.request.RequestCreator;
import com.example.picasso.util.PicassoUtil;

import java.lang.ref.ReferenceQueue;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;

public class Picasso {
    private static final int REQUEST_GC = 1;
    public static final int REQUEST_BATCH_COMPLETE = 2;
    private static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_GC:
                    Action action = (Action) msg.obj;
                    action.getPicasso().cancelExistingRequest(action.getTarget());
                    break;
                case REQUEST_BATCH_COMPLETE:
                    List<BitmapHunter> batch = (List<BitmapHunter>) msg.obj;
                    for (int i = 0; i < batch.size(); i ++) {
                        BitmapHunter hunter = batch.get(i);
                        hunter.getPicasso().complete(hunter);
                    }
                    break;
            }
        }
    };

    private CleanUpThread cleanUpThread;
    
    private static Picasso singleton;
    private Context context;
    private ReferenceQueue<Object> referenceQueue;
    private Cache cache;
    private Dispatcher dispatcher;
    private RequestHandler requestHandler;
    private boolean shutdown;
    private Map<Object, Action> targetToAction; // 存放所有请求的Action集合
    


    private Picasso(Context context, Cache cache, Dispatcher dispatcher,
                    ExecutorService service, RequestHandler requestHandler) {
        this.context = context;
        this.cache = cache;
        this.dispatcher = dispatcher;
        this.requestHandler = requestHandler;
        referenceQueue = new ReferenceQueue<>();
        targetToAction = new WeakHashMap<>();
        cleanUpThread = new CleanUpThread(handler, referenceQueue);
        cleanUpThread.start();
    }

    //
    public ReferenceQueue<Object> getReferenceQueue() {
        return referenceQueue;
    }

    public Context getContext() {
        return context;
    }

    public Bitmap getResultFromMemory(String key) {
        return cache.get(key);
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public RequestCreator load(Uri uri) {
        return new RequestCreator(this, uri);
    }

    public void clearCache() {
        cache.clear();
    }


    public void cancelRequest(ImageView target) {
        if (target == null) throw new IllegalArgumentException("target can not be null");
        cancelExistingRequest(target);
    }

    public void enqueueSubmit(Action action) {
        Object target = action.getTarget();
        if (target != null && targetToAction.get(target) != null) {
            cancelExistingRequest(target);
            targetToAction.put(target, action);
        }
        submit(action);
    }




    public void cancelExistingRequest(Object target) {
        PicassoUtil.checkMain();
        Action action = targetToAction.remove(target);
        if (action != null) {
            Log.d("TAG", "cancelExistingRequest: ");
            action.cancel();
            dispatcher.dispatchCancel(action);
        }
    }

    public void submit(Action action) {

        dispatcher.dispatchSubmit(action);
    }
    //


    public void complete(BitmapHunter hunter) {
        Action action = hunter.getAction();
        if (action == null) {
            Log.d("TAG", "complete: action is null");
            return;
        }
        if (action.isCancel()) {
            Log.d("TAG", "complete: action is canceled");
            return;
        }
        ImageView target = (ImageView) action.getTarget();
        if (target == null) {
            Log.d("TAG", "complete: target is null");
            return;
        }
        targetToAction.remove(action.getTarget());
        Bitmap result = hunter.getResult();
        action.complete(result);
    }


    public static Picasso with(Context context) {
        if (singleton == null) {
            synchronized (Picasso.class) {
                singleton = new Builder(context).build();
            }
        }
        return singleton;
    }

    public void shutdown() {
        if (this == singleton) throw new UnsupportedOperationException("can not shut down singleton");
        if (shutdown) return;
        cache.clear();
        cleanUpThread.interrupt();
        dispatcher.shutdown();
    }

    public static class Builder {

        Context context;
        ExecutorService service;
        RequestHandler requestHandler;
        Cache cache;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder service(ExecutorService service) {
            this.service = service;
            return this;
        }

        public Builder requestHandler(RequestHandler requestHandler) {
            this.requestHandler = requestHandler;
            return this;
        }

        public Builder memoryCache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Picasso build() {
            if (service == null) {
                service = new PicassoExecutor();
            }
            if (requestHandler == null) {
                requestHandler = new ContentHandler(context);
            }
            if (cache == null) {
                cache = new Cache();
            }
            Dispatcher dispatcher = new Dispatcher(handler, service, cache);
            return new Picasso(context, cache, dispatcher, service, requestHandler);
        }

    }

    static class CleanUpThread extends Thread {
        Handler handler;
        ReferenceQueue<Object> queue;

        CleanUpThread(Handler handler, ReferenceQueue<Object> queue) {
            this.handler = handler;
            this.queue = queue;
            setDaemon(true);
        }

        @Override
        public void run() {
            super.run();
            // 如果请求超时，取消请求
            while (true) {
                try {
                    Action.RequestWeakReference<?> weakReference =
                            (Action.RequestWeakReference<?>) queue.remove(PicassoUtil.CLEAN_UP_TIME);
                    if (weakReference != null) {
                        Message message = Message.obtain();
                        message.what = REQUEST_GC;
                        message.obj = weakReference.action;
                        handler.sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
