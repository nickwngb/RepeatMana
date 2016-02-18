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
 * Created by hao_jun on 2016/2/18.
 */
public class SendResponse extends AsyncTask<String, Integer, Integer> {
    public interface OnSendResponseListener {
        void finish(Integer result);
    }

    private final OnSendResponseListener mListener;
    private final HttpConnection conn;
    private String PRSNo;
    private String ResponseContent;
    private String ResponseDate;
    private String ResponseID;

    public SendResponse(HttpConnection conn, OnSendResponseListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.NoResponse;
        PRSNo = datas[0];
        ResponseContent = datas[1];
        ResponseDate = datas[2];
        ResponseID = datas[3];
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("PRSNo", PRSNo));
            params.add(new BasicNameValuePair("ResponseContent", ResponseContent));
            params.add(new BasicNameValuePair("ResponseDate", ResponseDate));
            params.add(new BasicNameValuePair("ResponseID", ResponseID));
            params.add(new BasicNameValuePair("ResponseRole", Code.Manager));
            JSONObject jobj = conn.PostGetJson(URLs.url_addresponse, params);
            if (jobj != null) {
                result = jobj.getInt("success");
            }
        } catch (JSONException e) {
            Log.i("SendResponse", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        mListener.finish(result);
    }
}
