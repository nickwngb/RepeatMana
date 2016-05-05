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
public class RegisterGCM extends AsyncTask<String, Integer, Integer> {
    public interface OnRegisterGCMListener {
        void finish(Integer result);
    }

    private OnRegisterGCMListener mListener;

    public RegisterGCM(OnRegisterGCMListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.NoResponse;
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("GCMid", datas[0]));
            params.add(new BasicNameValuePair("UserID", datas[1]));
            JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_gcm_register, params);
            if (jobj != null) {
                result = jobj.getInt("success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("RegisterGCM", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        mListener.finish(result);
    }
}
