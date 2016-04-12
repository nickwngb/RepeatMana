package com.goldgrother.repeatmana.Activity;

/**
 * Created by user on 2016/01/05.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.goldgrother.repeatmana.Adapter.ResponsesAdapter;
import com.goldgrother.repeatmana.Asyn.ChangeStatus;
import com.goldgrother.repeatmana.Asyn.LoadAllResponse;
import com.goldgrother.repeatmana.Asyn.SendResponse;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.MyDialog;
import com.goldgrother.repeatmana.Other.Hardware;
import com.goldgrother.repeatmana.Other.HttpConnection;
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
        LoadAllResponse(PRSNo + "");
    }

    private void LoadAllResponse(String PRSNo) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllResponse task = new LoadAllResponse(new LoadAllResponse.OnLoadAllResponseListener() {
                public void finish(Integer result, List<ProblemResponse> list) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            responses.clear();
                            responses.addAll(list);
                            refreshResponseContent();
                            scrollMyListViewToBottom();
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

    private void scrollMyListViewToBottom() {
        lv_responses.post(new Runnable() {
            public void run() {
                lv_responses.setSelection(lv_responses.getCount() - 1);
            }
        });
    }

    private void SendResponse(String... params) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            SendResponse task = new SendResponse(new SendResponse.OnSendResponseListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            et_content.setText("");
                            LoadAllResponse(PRSNo + "");
                            break;
                        case Code.Fail:
                            Toast.makeText(ctxt, "Fail", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute(params);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void ChangeStatus(String status) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            ChangeStatus task = new ChangeStatus(new ChangeStatus.OnChangeStatusListener() {
                @Override
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            Toast.makeText(ctxt, "Status is changed", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.Fail:
                            Toast.makeText(ctxt, "Status change fail", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                    }
                }
            }, PRSNo);
            task.execute(status);
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
        responses = new ArrayList<>();
        adapter = new ResponsesAdapter(ctxt, FLaborNo, CustomerNo, responses);
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
        lv_responses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyDialog.showTextDialog(ctxt, responses.get(position).getResponseContent());
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