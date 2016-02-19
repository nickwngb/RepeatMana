package com.goldgrother.repeatmana.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v on 2016/2/17.
 */


public class UploadPhoto extends AsyncTask<String, Integer, Integer> {
    public interface OnUpdatePhotoListener {
        void finish(Integer result);
    }

    private HttpConnection conn;
    private OnUpdatePhotoListener mListener;
    private String UserID;
    private String photo;

    public UploadPhoto(HttpConnection conn, OnUpdatePhotoListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.NoResponse;
        try {
            UserID = datas[0];
            photo = datas[1];
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("Role", Code.Manager));
            params.add(new BasicNameValuePair("CustomerNo", "12345"));
            params.add(new BasicNameValuePair("FLaborNo", "12345"));
            params.add(new BasicNameValuePair("LaborPhoto", "12345"));
            // Fake Data Useless
            params.add(new BasicNameValuePair("UserID", UserID));
            params.add(new BasicNameValuePair("UserPhoto", photo));

            JSONObject jobj = conn.PostGetJson(URLs.url_uploadimage, params);
            if (jobj != null) {
                result = jobj.getInt("success");
            }
        } catch (JSONException e) {
            Log.i("UploadPhoto", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer);
    }
}
