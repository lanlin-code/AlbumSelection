package com.example.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.example.picasso.action.Action;
import com.example.picasso.cache.Cache;
import com.example.picasso.dispatcher.Dispatcher;
import com.example.picasso.handler.RequestHandler;
import com.example.picasso.request.RequestCreator;
import com.example.picasso.util.PicassoUtil;

import java.lang.ref.ReferenceQueue;
import java.util.Map;

public class Picasso {
    private static Picasso singleton;
    private Context context;
    private ReferenceQueue<Object> referenceQueue;
    private Cache cache;
    private Dispatcher dispatcher;
    private RequestHandler requestHandler;
    private boolean shutdown;
    private Map<Object, Action> targetToAction; // 存放所有请求的Action集合

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




    private void cancelExistingRequest(Object target) {
        PicassoUtil.checkMain();
        Action action = targetToAction.remove(target);
        if (action != null) {
            Log.d("TAG", "cancelExistingRequest: ");
            action.cancel();
            dispatcher.dispatchCancel(action);
        }
    }

    private void submit(Action action) {

        dispatcher.dispatchSubmit(action);
    }
}
