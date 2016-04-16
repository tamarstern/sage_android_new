package com.sage.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

/**
 * Created by tamar.twena on 4/13/2016.
 */
public class ImageLruCacheContainer {

    private LruCache<String , Object> mMemoryCache;

    public ImageLruCacheContainer(Activity context) {
        final int memClass = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;

        mMemoryCache = new LruCache(cacheSize) ;

    }

    public void addBitmapToMemoryCache(String key, Drawable bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Drawable getBitmapFromMemCache(String key) {
        return (Drawable)mMemoryCache.get(key);
    }
}
