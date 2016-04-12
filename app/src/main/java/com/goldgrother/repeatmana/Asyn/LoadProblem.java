package com.goldgrother.repeatmana.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.ProblemRecord;
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
public class LoadProblem extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadProblemListener {
        void finish(Integer result, List<ProblemRecord> list);
    }

    private final OnLoadProblemListener mListener;
    private final HttpConnection conn;
    private String startDay;
    private String endDay;
    private String status;
    private String dormId;
    private List<ProblemRecord> list;

    public LoadProblem(HttpConnection conn, OnLoadProblemListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
        this.list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.NoResponse;
        startDay = datas[0];
        endDay = datas[1];
        status = datas[2];
        dormId = datas[3];
        Log.d("LoadProblem",startDay);
        Log.d("LoadProblem",endDay);
        Log.d("LoadProblem",status);
        Log.d("LoadProblem",dormId);
        try {
            // put "phone" post out, get json
            List<NameValuePair> postFields = new ArrayList<>();
            postFields.add(new BasicNameValuePair("Role", Code.Manager));
            postFields.add(new BasicNameValuePair("startday", startDay));
            postFields.add(new BasicNameValuePair("endday", endDay));
            postFields.add(new BasicNameValuePair("status", status));
            postFields.add(new BasicNameValuePair("dormid", dormId));
            // Fake Data Useless
            postFields.add(new BasicNameValuePair("FLaborNo", "00001"));
            postFields.add(new BasicNameValuePair("CustomerNo", "00001"));

            JSONObject jobj = conn.PostGetJson(URLs.url_allproblem, postFields);
            if (jobj != null) {
                result = jobj.getInt("success");
                if (result == Code.Success) {
                    JSONArray array = jobj.getJSONArray("fproblems");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject ajobj = array.getJSONObject(i);
                        ProblemRecord fproblem = new ProblemRecord();
                        fproblem.setPRSNo(ajobj.getInt("PRSNo"));
                        fproblem.setCustomerNo(ajobj.getString("CustomerNo"));
                        fproblem.setFLaborNo(ajobj.getString("FLaborNo"));
                        fproblem.setProblemStatus(ajobj.getString("ProblemStatus"));
                        fproblem.setSatisfactionDegree(ajobj.getString("SatisfactionDegree"));
                        fproblem.setWorkNo(ajobj.getString("WorkerNo"));
                        list.add(fproblem);
                    }
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
        mListener.finish(result, list);
    }
}
