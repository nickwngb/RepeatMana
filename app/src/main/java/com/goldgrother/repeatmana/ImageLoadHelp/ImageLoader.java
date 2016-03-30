package com.goldgrother.repeatmana.ImageLoadHelp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


import android.os.AsyncTask;
import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.goldgrother.repeatmana.Other.BitmapTransformer;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.URLs;
import com.goldgrother.repeatmana.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageLoader {

    // Initialize MemoryCache
    MemoryCache memoryCache = new MemoryCache();

    FileCache fileCache;

    //Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());

    //handler to display images in UI thread
    Handler handler = new Handler();

    public ImageLoader(Context context) {

        //fileCache = new FileCache(context);

        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.

    }

    // default image show in list (Before online image download)
    final int stub_id = R.drawable.people;

    public void DisplayImage(String customerNo, String flaborNo, ImageView imageView) {
        //Store image and url in Map
        imageViews.put(imageView, customerNo + flaborNo);

        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        Bitmap bitmap = memoryCache.get(customerNo + flaborNo);

        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap);
        } else {
            //queue Photo to download from url
            queuePhoto(customerNo, flaborNo, imageView);

            //Before downloading image show default image
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String customerNo, String flaborNo, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(customerNo, flaborNo, imageView);
        //Check if image already downloaded
        if (imageViewReused(p))
            return;

        new LoadPhotoTask(p).execute(customerNo, flaborNo);


    }

    //Task for the queue
    private class PhotoToLoad {
        public String customerNo;
        public String flaborNo;
        public ImageView imageView;

        public PhotoToLoad(String customerNo, String flaborNo, ImageView i) {
            this.customerNo = customerNo;
            this.flaborNo = flaborNo;
            this.imageView = i;
        }
    }

    boolean imageViewReused(PhotoToLoad p) {

        String tag = imageViews.get(p.imageView);
        //Check url is already exist in imageViews MAP
        if (tag == null || !tag.equals(p.customerNo + p.flaborNo))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;

            // Show bitmap on UI
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        fileCache.clear();
    }

    private class LoadPhotoTask extends AsyncTask<String, Integer, Bitmap> {
        private PhotoToLoad p;

        public LoadPhotoTask(PhotoToLoad p) {
            this.p = p;
        }

        @Override
        protected Bitmap doInBackground(String... datas) {
            Integer result;
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("Role", "1"));
                params.add(new BasicNameValuePair("CustomerNo", datas[0]));
                params.add(new BasicNameValuePair("FLaborNo", datas[1]));
                params.add(new BasicNameValuePair("UserID", "12345"));

                JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_loadimage, params);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
                        String base64 = jobj.getString("photo");
                        return BitmapTransformer.Base64ToBitmap(base64);
                    }
                }
            } catch (JSONException e) {
                Log.i("JSONException", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null)
                return;
            memoryCache.put(p.customerNo + p.flaborNo, bitmap);

            if (imageViewReused(p))
                return;

            // Get bitmap to display
            BitmapDisplayer bd = new BitmapDisplayer(bitmap, p);

            handler.post(bd);
        }
    }

}