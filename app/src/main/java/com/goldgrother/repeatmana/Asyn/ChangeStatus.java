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
 * Created by asus on 2016/4/8.
 */
public class ChangeStatus extends AsyncTask<String, Integer, Integer> {
    public interface OnChangeStatusListener {
        void finish(Integer result);
    }

    private OnChangeStatusListener mListener;
    private Integer PRSNo;
    private String status;

    public ChangeStatus(OnChangeStatusListener mListener, int PRSNo) {
        this.mListener = mListener;
        this.PRSNo = PRSNo;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.NoResponse;

        try {
            Log.d("ChangeStatus", datas[0]);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("PRSNo", PRSNo + ""));
            params.add(new BasicNameValuePair("status", datas[0]));

            JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_changestatus, params);
            if (jobj != null) {
                result = jobj.getInt("success");
            }
        } catch (JSONException e) {
            Log.i("ChangeStatus", e.toString());
        }
        return result;
    }
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer);
    }
}
