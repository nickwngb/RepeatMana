package com.goldgrother.repeatmana.Activity;

/**
 * Created by user on 2016/01/05.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.URLs;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Act_Problem extends Activity {
    //
    private Context ctxt = Act_Problem.this;
    private UserAccount user;
    private HttpConnection conn;
    private Resources res;
    private ProblemRecord pr;
    // UI
    private TextView txt_problem_createdate, txt_customercontent, txt_problem_repeatedate;
    private EditText et_manacontent;
    private LinearLayout ll_givestart;
    private Button bt_send;
    private RatingBar rb_score;
    // other


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_problem);
        getExtras();
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void UpdateStartTask(String... datas) {
        if (Uti.isNetWork(ctxt)) {
            new UpdateStartTask().execute(datas);
        } else {
            Toast.makeText(ctxt, res.getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class UpdateStartTask extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog pDialog;
        private String PRSNo, ResponseResult, ResponseDate, ResponseId, ProblemStatus;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            PRSNo = String.valueOf(pr.getPRSNo());
            ResponseResult = et_manacontent.getText().toString();
            ResponseDate = getCurrentDateTime();
            ResponseId = user.getUserID();
            ProblemStatus = "2";
            Log.d("UpdateStartTask", PRSNo);
            Log.d("UpdateStartTask", ResponseResult);
            Log.d("UpdateStartTask", ResponseDate + " " + ResponseId + " " + ProblemStatus);
        }

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = Code.NoResponse;
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("PRSNo", PRSNo));
                postFields.add(new BasicNameValuePair("ResponseResult", ResponseResult));
                postFields.add(new BasicNameValuePair("ResponseDate", ResponseDate));
                postFields.add(new BasicNameValuePair("ResponseId", ResponseId));
                postFields.add(new BasicNameValuePair("ProblemStatus", ProblemStatus));


                JSONObject jobj = conn.PostGetJson(URLs.url_repeatproblem, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            pDialog.dismiss();
            switch (result) {
                case Code.Success:
                    Toast.makeText(ctxt, "Repeat Success", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    break;
                case Code.Fail:
                    Toast.makeText(ctxt, "Repeat Success", Toast.LENGTH_SHORT).show();
                    break;
                case Code.NoResponse:
                    Toast.makeText(ctxt, "Server no response", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private boolean isVaild() {
        int PRSNo = pr.getPRSNo();
        String ResponseResult = et_manacontent.getText().toString();
        String ResponseDate = getCurrentDateTime();
        String ResponseID = user.getUserID();
        String ProblemStatus = "2";
        if (PRSNo <= 0) {
            return false;
        }
        if (ResponseResult.isEmpty()) {
            Toast.makeText(ctxt, "Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ResponseID.isEmpty()) {
            Toast.makeText(ctxt, "Id error", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }

    private void InitialSomething() {
        user = UserAccount.getUserAccount();
        res = getResources();
        conn = new HttpConnection();
    }

    private void InitialUI() {
        ll_givestart = (LinearLayout) findViewById(R.id.ll_givestart);
        bt_send = (Button) findViewById(R.id.bt_send);
        rb_score = (RatingBar) findViewById(R.id.rb_score);
        txt_problem_createdate = (TextView) findViewById(R.id.txt_problem_createdate);
        txt_customercontent = (TextView) findViewById(R.id.txt_customercontent);
        txt_problem_repeatedate = (TextView) findViewById(R.id.txt_problem_repeatedate);
        et_manacontent = (EditText) findViewById(R.id.et_problem_manacontent);
    }

    private void InitialAction() {
        txt_problem_createdate.setText(pr.getCreateProblemDate());
        txt_customercontent.setText(pr.getProblemDescription());
        txt_problem_repeatedate.setText(pr.getResponseDate());
        et_manacontent.setText(pr.getResponseResult());
        float rating = 5f;
        try {
            rating = Float.valueOf(pr.getSatisfactionDegree());
        } catch (Exception ex) {
        }
        rb_score.setRating(rating);
        if (pr.getProblemStatus().equals(Code.Completed)) {
            rb_score.setVisibility(View.VISIBLE);
        }

        bt_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isVaild()) {
                    UpdateStartTask();
                }
            }
        });
    }

    private void getExtras() {
        Bundle b = getIntent().getExtras();
        pr = (ProblemRecord) b.getSerializable("ProblemRecord");
    }
}