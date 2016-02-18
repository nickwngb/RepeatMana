package com.goldgrother.repeatmana.Activity;

/**
 * Created by user on 2016/01/05.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.goldgrother.repeatmana.Adapter.ResponsesAdapter;
import com.goldgrother.repeatmana.Asyn.LoadAllResponse;
import com.goldgrother.repeatmana.Asyn.SendResponse;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.FreeDialog;
import com.goldgrother.repeatmana.Other.Hardware;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.ProblemResponse;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Act_Responses extends Activity {
    //
    private Context ctxt = Act_Responses.this;
    private UserAccount user;
    private HttpConnection conn;
    private Resources res;
    // UI
    private ListView lv_responses;
    // adapter
    private ResponsesAdapter adapter;
    private EditText et_content;
    private Button bt_send;
    // other
    private int PRSNo;
    private String FLaborNo;
    private String CustomerNo;
    private List<ProblemResponse> responses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_responses);
        getExtras();
        InitialSomething();
        InitialUI();
        InitialAction();
        getExtras();
        LoadAllResponse(PRSNo + "");
    }

    private void LoadAllResponse(String PRSNo) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = FreeDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllResponse task = new LoadAllResponse(conn, new LoadAllResponse.OnLoadAllResponseListener() {
                public void finish(Integer result, List<ProblemResponse> list) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            responses = list;
                            Toast.makeText(ctxt, "Refresh", Toast.LENGTH_SHORT).show();
                            refreshResponseContent();
                            break;
                        case Code.Empty:
                            Toast.makeText(ctxt, "Empty", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute(PRSNo);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshResponseContent() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void SendResponse(String... params) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = FreeDialog.getProgressDialog(ctxt, "Loading...");
            SendResponse task = new SendResponse(conn, new SendResponse.OnSendResponseListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            LoadAllResponse(PRSNo + "");
                            break;
                        case Code.Fail:
                            break;
                    }
                }
            });
            task.execute(params);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private void InitialSomething() {
        user = UserAccount.getUserAccount();
        res = getResources();
        conn = new HttpConnection();
        responses = new ArrayList<>();
        adapter = new ResponsesAdapter(ctxt,FLaborNo,CustomerNo, responses);
    }

    private void InitialUI() {
        lv_responses = (ListView) findViewById(R.id.lv_responses);
        et_content = (EditText) findViewById(R.id.et_responses_content);
        bt_send = (Button) findViewById(R.id.bt_responses_send);
    }

    private void InitialAction() {
        bt_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Hardware.closeKeyBoard(ctxt, view);
                String content = et_content.getText().toString();
                String datetime = getCurrentDateTime();
                String id = user.getUserID();
                SendResponse(PRSNo + "", content, datetime, id);
            }
        });
        lv_responses.setAdapter(adapter);
    }

    private void getExtras() {
        Intent it = getIntent();
        PRSNo = it.getIntExtra("PRSNo", 0);
        FLaborNo = it.getStringExtra("FLaborNo");
        CustomerNo = it.getStringExtra("CustomerNo");
    }
}