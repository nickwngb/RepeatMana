package com.goldgrother.repeatmana.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.goldgrother.repeatmana.Adapter.ListAdapter;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.URLs;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by v on 2016/1/1.
 */
public class Act_MainScreen extends AppCompatActivity {

    private Context ctxt = Act_MainScreen.this;
    private HttpConnection con;
    public UserAccount user = Act_Login.user;
    // UI
    private Button bt_untreated, bt_processing, bt_completed;
    private ListView lv_problems;
    // Adapter
    private ListAdapter adapter;
    // Other
    private List<ProblemRecord> problemlist;
    // Status Code
    private static final String Untreated = "0";
    private static final String Processing = "1";
    private static final String Completed = "2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mainscreen);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void LoadingProblem(String status) {
        if (Uti.isNetWork(ctxt)) {
            new LoadingProblem().execute(status);
        } else {

        }
    }

    private class LoadingProblem extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog mDialog;
        private String status;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(ctxt);
            mDialog.setMessage("Loading");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = Code.NoResponse;
            status = params[0];
            problemlist.clear();
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("mday", getCurrentDate()));
                postFields.add(new BasicNameValuePair("status", status));
                JSONObject jobj = con.PostGetJson(URLs.url_loadingproblem, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
                        JSONArray array = jobj.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject ajobj = array.getJSONObject(i);
                            ProblemRecord fproblem = new ProblemRecord();
                            fproblem.setPRSNo(ajobj.getInt("PRSNo"));
                            fproblem.setCustomerNo(ajobj.getString("CustomerNo"));
                            fproblem.setFLaborNo(ajobj.getString("FLaborNo"));
                            fproblem.setProblemDescription(ajobj.getString("ProblemDescription"));
                            fproblem.setCreateProblemDate(ajobj.getString("CreateProblemDate"));
                            fproblem.setResponseResult(ajobj.getString("ResponseResult"));
                            fproblem.setResponseDate(ajobj.getString("ResponseDate"));
                            fproblem.setResponseID(ajobj.getString("ResponseID"));
                            fproblem.setProblemStatus(ajobj.getString("ProblemStatus"));
                            fproblem.setSatisfactionDegree(ajobj.getString("SatisfactionDegree"));
                            problemlist.add(fproblem);
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
            mDialog.dismiss();
            Log.i("LoadingProblem", "Result:" + result);
            switch (result) {
                case Code.Success:
                    refreshList();
                    break;
                case Code.Empty:
                    refreshList();
                    break;
                case Code.NoResponse:
                    Toast.makeText(ctxt, "Server no response", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
    }

    private void InitialAction() {
        bt_untreated.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoadingProblem(Untreated);
            }
        });
        bt_processing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoadingProblem(Processing);
            }
        });
        bt_completed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoadingProblem(Completed);
            }
        });
        lv_problems.setAdapter(adapter);
    }

    private void InitialUI() {
        bt_untreated = (Button) findViewById(R.id.bt_main_untreated);
        bt_processing = (Button) findViewById(R.id.bt_main_processing);
        bt_completed = (Button) findViewById(R.id.bt_main_completed);
        lv_problems = (ListView) findViewById(R.id.lv_problems);
    }

    private void InitialSomething() {
        con = new HttpConnection();
        problemlist = new ArrayList<>();
        adapter = new ListAdapter(ctxt, problemlist);
    }
}
