package com.goldgrother.repeatmana.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.goldgrother.repeatmana.Adapter.ExpandListAdapter;
import com.goldgrother.repeatmana.Adapter.MyListAdapter;
import com.goldgrother.repeatmana.Asyn.LoadProblem;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.FreeDialog;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.URLs;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.Other.Worker;
import com.goldgrother.repeatmana.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by v on 2016/1/1.
 */
public class Act_MainScreen extends AppCompatActivity {

    private Context ctxt = Act_MainScreen.this;
    private HttpConnection con;
    private UserAccount user;
    private final int ActProblem = 0;
    // UI
    private Button bt_untreated, bt_processing, bt_completed;
    private ListView lv_problems;
    private ExpandableListView elv_workers;
    // Adapter
    private MyListAdapter list_adapter;
    private ExpandListAdapter exlist_adapter;
    // Other
    private List<ProblemRecord> problemlist;
    private List<Worker> list_workers;
    private String LastClickStatus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mainscreen);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void LoadingProblem(final String status) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = FreeDialog.getProgressDialog(ctxt, "Loading...");
            LoadProblem task = new LoadProblem(con, new LoadProblem.OnLoadProblemListener() {
                public void finish(Integer result, List<ProblemRecord> list) {
                    pd.dismiss();
                    Log.i("LoadingProblem", "Result:" + result);
                    switch (result) {
                        case Code.Success:
                        case Code.Empty:
                            if (status.equals(Code.Completed)) {
                                refreshExpandList();
                            } else {
                                refreshList();
                            }
                            break;
                        case Code.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute(getCurrentDateStart(status), getCurrentDateEnd(), status, user.getDormID());
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshList() {
        elv_workers.setVisibility(View.GONE);
        lv_problems.setVisibility(View.VISIBLE);
        if (list_adapter != null) {
            list_adapter.notifyDataSetChanged();
        }
    }

    private void refreshExpandList() {
        lv_problems.setVisibility(View.GONE);
        elv_workers.setVisibility(View.VISIBLE);
        // clear !!
        list_workers.clear();
        Set<String> wNos = new HashSet<>();
        // put workerNo to Set (no repeat)
        for (ProblemRecord pr : problemlist) {
            wNos.add(pr.getWorkNo());
        }
        // put workerNo to Worker Instances
        for (String no : wNos) {
            Worker w = new Worker();
            w.setWorkerNo(no);
            list_workers.add(w);
        }
        // put problems to conform workerNo
        for (Worker w : list_workers) {
            List<ProblemRecord> items = new ArrayList<>();
            for (ProblemRecord pr : problemlist) {
                if (w.getWorkerNo().equals(pr.getWorkNo())) {
                    items.add(pr);
                }
            }
            w.setItems(items);
        }
        // refresh !
        if (exlist_adapter != null) {
            exlist_adapter.notifyDataSetChanged();
        }
    }


    private String getCurrentDateStart(String status) {
        Calendar calendar = Calendar.getInstance();
        if (status.equals(Code.Completed)) {
            calendar.add(Calendar.MONTH, -12);
        }
        return new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime()) + " 00:00:00";
    }

    private String getCurrentDateEnd() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
        return sdFormat.format(new java.util.Date()) + " 23:59:59";
    }

    private void InitialAction() {
        bt_untreated.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LastClickStatus = Code.Untreated;
                LoadingProblem(Code.Untreated);
            }
        });
        bt_processing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LastClickStatus = Code.Processing;
                LoadingProblem(Code.Processing);
            }
        });
        bt_completed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LastClickStatus = Code.Completed;
                LoadingProblem(Code.Completed);
            }
        });
        // ListView setting
        lv_problems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProblemRecord pr = problemlist.get(position);
                Intent it = new Intent(ctxt, Act_Problem.class);
                it.putExtra("PRSNo", pr.getPRSNo());
                startActivityForResult(it, ActProblem);
            }
        });
        lv_problems.setAdapter(list_adapter);
        // ExpandableListView setting
        elv_workers.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                List<ProblemRecord> list = list_workers.get(groupPosition).getItems(); // this worker problems
                ProblemRecord pr = list.get(childPosition);
                Intent it = new Intent(ctxt, Act_Problem.class);
                it.putExtra("PRSNo", pr.getPRSNo());
                startActivity(it);
                return false;
            }
        });
        elv_workers.setAdapter(exlist_adapter);
    }

    private void InitialUI() {
        bt_untreated = (Button) findViewById(R.id.bt_main_untreated);
        bt_processing = (Button) findViewById(R.id.bt_main_processing);
        bt_completed = (Button) findViewById(R.id.bt_main_completed);
        lv_problems = (ListView) findViewById(R.id.lv_problems);
        elv_workers = (ExpandableListView) findViewById(R.id.elv_workers);
    }

    private void InitialSomething() {
        user = UserAccount.getUserAccount();
        con = new HttpConnection();
        problemlist = new ArrayList<>();
        list_workers = new ArrayList<>();
        list_adapter = new MyListAdapter(ctxt, problemlist);
        exlist_adapter = new ExpandListAdapter(ctxt, list_workers);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActProblem) {
            switch (resultCode) {
                case RESULT_OK:
                    LoadingProblem(LastClickStatus);
                    break;
            }
        }
    }

    private long lastpresstime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastpresstime < 2000) {
            super.onBackPressed();
        } else {
            lastpresstime = System.currentTimeMillis();
            Toast.makeText(ctxt, "Press again to leave", Toast.LENGTH_SHORT).show();
        }
    }
}
