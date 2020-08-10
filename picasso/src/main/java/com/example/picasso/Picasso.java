package com.example.picasso;

import android.content.Context;

import com.example.picasso.action.Action;
import com.example.picasso.cache.Cache;
import com.example.picasso.dispatcher.Dispatcher;
import com.example.picasso.handler.RequestHandler;

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
}
