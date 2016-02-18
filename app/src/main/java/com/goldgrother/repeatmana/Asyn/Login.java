package com.goldgrother.repeatmana.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao_jun on 2016/2/17.
 */
public class Login extends AsyncTask<String, Integer, Integer> {
    public interface OnLoginListener {
        void finish(Integer result,String UserPhoto,String DormID);
    }

    private final OnLoginListener mListener;
    private final HttpConnection conn;
    private String account;
    private String password;
    private String UserPhoto;
    private String DormID;

    public Login(HttpConnection conn, OnLoginListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.NoResponse;
        account = datas[0];
        password = datas[1];
        try {
            // put "phone" post out, get json
            List<NameValuePair> postFields = new ArrayList<>();
            postFields.add(new BasicNameValuePair("account", account));
            postFields.add(new BasicNameValuePair("password", password));
            JSONObject jobj = conn.PostGetJson(URLs.url_login, postFields);
            if (jobj != null) {
                result = jobj.getInt("success");
                if (result == Code.Success) {
                    JSONArray array = jobj.getJSONArray("result");
                    JSONObject minfo = array.getJSONObject(0);
                    UserPhoto = minfo.getString("UserPhoto");
                    DormID = minfo.getString("DormID");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        mListener.finish(result,UserPhoto,DormID);
    }
}
