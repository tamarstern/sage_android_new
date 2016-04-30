package com.sage.application;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class TCImageLoader  {
    private TCLruCache cache;

    public TCImageLoader(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        int maxKb = am.getMemoryClass() * 1024;
        int limitKb = maxKb / 8; // 1/8th of total ram
        cache = new TCLruCache(limitKb);
    }

    public void display(String url, ImageView imageview, ProgressBar progressBar, int defaultResource) {
        Bitmap image = cache.get(url);
        if (image != null) {
            imageview.setImageBitmap(image);
        }
        else {
            new SetImageTask(imageview,progressBar, defaultResource).execute(url);
        }
    }

    private class TCLruCache extends LruCache<String, Bitmap> {

        public TCLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            int kbOfBitmap = value.getByteCount() / 1024;
            return kbOfBitmap;
        }
    }

    private class SetImageTask extends AsyncTask<String, Void, Integer> {
        private ImageView imageview;
        private Bitmap bmp;
        private int defaultResource;
        private ProgressBar progressBar;


        public SetImageTask(ImageView imageview, ProgressBar progressBar, int defaultResource) {

            this.imageview = imageview;
            this.defaultResource = defaultResource;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            imageview.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            try {
                bmp = getBitmapFromURL(url);
                if (bmp != null) {
                    cache.put(url, bmp);
                }
                else {
                    return 0;
                }
            } catch (Exception e) {
                Log.e("TCLoaderFailed", "TC Image Loader failed", e);
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);
            if(result == 0) {
                imageview.setImageResource(defaultResource);
            }
            else if (result == 1) {
                imageview.setImageBitmap(bmp);
            }
            imageview.setVisibility(View.VISIBLE);
            super.onPostExecute(result);
        }

        private Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection
                        = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                Log.e("TCLoaderFailed", "failed get bitmap from url ", e);
                return null;
            }
        }

    }


}