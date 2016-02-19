package com.goldgrother.repeatmana.Asyn;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.goldgrother.repeatmana.Other.BitmapTransformer;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hao_jun on 2016/2/17.
 */
public class LoadPhoto extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadPhotoListener {
        void finish();
    }

    private WeakReference<CircleImageView> rf_circleImg;
    private final OnLoadPhotoListener mListener;
    private final HttpConnection conn;
    private String role;
    private Bitmap bitmap;

    public LoadPhoto(CircleImageView circleImageView, HttpConnection conn, OnLoadPhotoListener mListener) {
        this.rf_circleImg = new WeakReference<>(circleImageView);
        this.conn = conn;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.NoResponse;
        try {
            role = datas[0];
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("Role", role));
            if (role.equals(Code.Flabor)) {
                params.add(new BasicNameValuePair("CustomerNo", datas[1]));
                params.add(new BasicNameValuePair("FLaborNo", datas[2]));
                params.add(new BasicNameValuePair("UserID", "12345"));
            } else if (role.equals(Code.Manager)) {
                params.add(new BasicNameValuePair("CustomerNo", "12345"));
                params.add(new BasicNameValuePair("FLaborNo", "12345"));
                params.add(new BasicNameValuePair("UserID", datas[1]));
            }
            HttpConnection conn = new HttpConnection();
            JSONObject jobj = conn.PostGetJson(URLs.url_loadimage, params);
            if (jobj != null) {
                result = jobj.getInt("success");
                if (result == Code.Success) {
                    String base64 = jobj.getString("photo");
                    bitmap = BitmapTransformer.Base64ToBitmap(base64);
                }
            }
        } catch (JSONException e) {
            Log.i("JSONException", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == Code.Success) {
            if (rf_circleImg != null) {
                ImageView imageView = rf_circleImg.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        imageView.setTag(1);
                        mListener.finish();
                    }
                }
            }
        }

    }
}
