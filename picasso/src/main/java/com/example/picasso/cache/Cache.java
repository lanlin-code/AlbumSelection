package com.example.picasso.cache;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

public class Cache {
    private LruCache<String, Bitmap> cache;

    public Cache() {
        int size = (int) (Runtime.getRuntime().freeMemory()/8);
        cache = new androidx.collection.LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };
    }

    public void put(String key, Bitmap value) {
        cache.put(key, value);
    }

    public Bitmap get(String key) {
        return cache.get(key);
    }

    public void clear() {
        cache.evictAll();
    }
}
