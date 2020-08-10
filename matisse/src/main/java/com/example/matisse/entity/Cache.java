package com.example.matisse.entity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LruCache;

public class Cache {
    private LruCache<Uri, Bitmap> cache;

    public Cache() {
        int maxSize = (int) (Runtime.getRuntime().freeMemory()/8);
        cache = new LruCache<Uri, Bitmap>(maxSize){
            @Override
            protected int sizeOf(Uri key, Bitmap value) {
                return value.getHeight()*value.getRowBytes();
            }
        };
    }

    public Cache(int size) {
        if (size < 0) throw new IllegalArgumentException("you can not make size < 0");
        cache = new LruCache<Uri, Bitmap>(size);
    }


    public Bitmap get(Uri key) {
        return cache.get(key);
    }

    public void put(Uri key, Bitmap value) {
        cache.put(key, value);
    }

}
