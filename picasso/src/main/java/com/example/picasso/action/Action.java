package com.example.picasso.action;

import android.graphics.Bitmap;

import com.example.picasso.Picasso;
import com.example.picasso.request.Request;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public abstract class  Action<T> {
    private Picasso picasso;
    private RequestWeakReference<T> target;
    private int errorId;
    private Request request;
    private Object tag;
    private String key; // hunter表，cache里的键
    private boolean cancel;

    public Action(Picasso picasso, T target,
                  int errorId, Request request, Object tag, String key) {
        this.picasso = picasso;
        this.target = target == null ? null : new RequestWeakReference<T>(this, target, picasso.getReferenceQueue());
        this.errorId = errorId;
        this.request = request;
        this.tag = tag == null ? this : tag;
        this.key = key;
    }

    public Request getRequest() {
        return request;
    }

    public T getTarget() {
        return target == null ? null : target.get();
    }

    public Picasso getPicasso() {
        return picasso;
    }

    public Object getTag() {
        return tag;
    }

    public String getKey() {
        return key;
    }

    int getErrorId() {
        return errorId;
    }

    public void cancel() {
        cancel = true;
    }

    public boolean isCancel() {
        return cancel;
    }

    public abstract void complete(Bitmap result);
    public abstract void onError();
    public abstract void onCancel();


    public static class RequestWeakReference<T> extends WeakReference<T> {
        public Action action;

        RequestWeakReference(Action action, T referent, ReferenceQueue<? super T> q) {
            super(referent, q);
            this.action = action;
        }

    }
}
