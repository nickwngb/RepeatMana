package com.goldgrother.repeatmana.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.goldgrother.repeatmana.Adapter.ExpandListAdapter;
import com.goldgrother.repeatmana.Adapter.ProblemListAdapter;
import com.goldgrother.repeatmana.Asyn.LoadAllLastestResponse;
import com.goldgrother.repeatmana.Asyn.LoadProblem;
import com.goldgrother.repeatmana.Asyn.RegisterGCM;
import com.goldgrother.repeatmana.Asyn.UploadPhoto;
import com.goldgrother.repeatmana.GcmForGB.GoldBrotherGCM;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.MyDialog;
import com.goldgrother.repeatmana.Other.Hardware;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.ProblemResponse;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.Other.Worker;
import com.goldgrother.repeatmana.R;
import com.goldgrother.repeatmana.Receiver.RefreshReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by v on 2016/1/1.
 */
public class Act_MainScreen extends AppCompatActivity implements GoldBrotherGCM.MagicLenGCMListener, RefreshReceiver.OnrefreshListener, View.OnClickListener {

    private Context ctxt = Act_MainScreen.this;
    private HttpConnection con;
    private UserAccount user;
    private GoldBrotherGCM mGBGCM;
    private RefreshReceiver mRefreshReceiver;

    private static final int ActResponse = 0;
    private static final int PICK_PICTURE = 1;
    private static final int TRIM_PICTURE = 2;
    // UI
    private Button bt_untreated, bt_processing, bt_completed;
    private ImageView bt_image;
    private ListView lv_problems;
    private ExpandableListView elv_workers;
    // Adapter
    private ProblemListAdapter list_adapter;
    private ExpandListAdapter exlist_adapter;
    // Other
    private List<ProblemRecord> problemlist;
    private List<ProblemResponse> responselist;
    private List<Worker> list_workers;
    private String LastClickStatus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mainscreen);
        InitialSomething();
        InitialUI();
        InitialAction();
        LoadingProblem(Code.Untreated);
    }

    // implements methods
    public void gcmRegistered(boolean successfull, String regID) {
        if (successfull) {
            Log.d("GCM", "GetFromGoogle:" + regID);
            RegisterGCMTask(regID);
        }
    }

    public boolean gcmSendRegistrationIdToAppServer(String regID) {
        return true;
    }

    private void RegisterGCMTask(String id) {
        if (Uti.isNetWork(ctxt)) {
            RegisterGCM task = new RegisterGCM(new RegisterGCM.OnRegisterGCMListener() {
                public void finish(Integer result) {
                    Log.d("GCM", "RegisterResult:" + result);
                }
            });
            task.execute(id, user.getUserID());
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }


    private void LoadingProblem(final String status) {

        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadProblem task = new LoadProblem(con, new LoadProblem.OnLoadProblemListener() {
                public void finish(Integer result, List<ProblemRecord> list) {
                    pd.dismiss();
                    problemlist.clear();
                    problemlist.addAll(list);
                    Log.i("LoadingProblem", "Result:" + result);
                    switch (result) {
                        case Code.Success:
                            Log.i("LoadingProblem", "111");
                            LoadAllLastestResponse(status);
                            break;
                        case Code.Empty:
                            Log.i("LoadingProblem", "222");
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

    private void LoadAllLastestResponse(final String status) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllLastestResponse task = new LoadAllLastestResponse(con, new LoadAllLastestResponse.OnLoadAllResponseListener() {
                public void finish(Integer result, List<ProblemResponse> list) {
                    pd.dismiss();
                    Log.d("LoadAllLastestResponse", "Result:" + result);
                    switch (result) {
                        case Code.Success:
                        case Code.Empty:
                            responselist = list;
                            PairWithRecord();
                            if (status.equals(Code.Completed)) {
                                refreshExpandList();
                            } else {
                                refreshList();
                            }
                            break;
                        case Code.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
            List<Integer> PRSNos = new ArrayList<>();
            for (ProblemRecord pr : problemlist) {
                PRSNos.add(pr.getPRSNo());
            }
            task.execute(PRSNos);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void PairWithRecord() {
        for (ProblemResponse rs : responselist) {
            int prsno = rs.getPRSNo();
            for (ProblemRecord pr : problemlist) {
                if (prsno == pr.getPRSNo()) {
                    pr.setResponseContent(rs.getResponseContent());
                    pr.setResponseDate(rs.getResponseDate());
                    pr.setResponseID(rs.getResponseID());
                    pr.setResponseRole(rs.getResponseRole());
                    break;
                }
            }
        }
    }

    private void refreshList() {
        elv_workers.setVisibility(View.GONE);
        lv_problems.setVisibility(View.VISIBLE);
        sortByDate();
        if (list_adapter != null) {
            list_adapter.notifyDataSetChanged();
        }
    }

    private void sortByDate() {
        for (int i = 0; i < problemlist.size() - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < problemlist.size() - i - 1; j++) {
                String a = problemlist.get(j).getResponseDate();
                String b = problemlist.get(j + 1).getResponseDate();
                //設定日期格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date1 = sdf.parse(a);
                    Date date2 = sdf.parse(b);
                    if (date1.before(date2)) {
                        Collections.swap(problemlist, j, j + 1);
                        swapped = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!swapped) {
                break;
            }
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
        //if (status.equals(Code.Completed)) {
        calendar.add(Calendar.MONTH, -12);
        //}
        return new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime()) + " 00:00:00";
    }

    private String getCurrentDateEnd() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
        return sdFormat.format(new java.util.Date()) + " 23:59:59";
    }

    private void UploadPhoto(Bitmap photo) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog fd = MyDialog.getProgressDialog(ctxt, "Loading...");
            UploadPhoto task = new UploadPhoto(con, new UploadPhoto.OnUpdatePhotoListener() {
                public void finish(Integer result) {
                    fd.dismiss();
                    Log.i("UploadPhoto", "Result " + result);
                    switch (result) {
                        case Code.Success:
                            Toast.makeText(ctxt, "Upload Success", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.Fail:
                            Toast.makeText(ctxt, "Upload Fail", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, photo);
            task.execute(Code.Manager, user.getUserID());
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_main_untreated:
                LastClickStatus = Code.Untreated;
                LoadingProblem(LastClickStatus);
                break;
            case R.id.bt_main_processing:
                LastClickStatus = Code.Processing;
                LoadingProblem(Code.Processing);
                break;
            case R.id.bt_main_completed:
                LastClickStatus = Code.Completed;
                LoadingProblem(Code.Completed);
                break;
        }
        changeButtonColor();
    }

    private void changeButtonColor() {
        int selected = getResources().getColor(R.color.tabSelected);
        int unSelected = getResources().getColor(R.color.tabUnselected);
        if (LastClickStatus.equals(Code.Untreated)) {
            bt_untreated.setTextColor(selected);
            bt_processing.setTextColor(unSelected);
            bt_completed.setTextColor(unSelected);
        } else if (LastClickStatus.equals(Code.Processing)) {
            bt_untreated.setTextColor(unSelected);
            bt_processing.setTextColor(selected);
            bt_completed.setTextColor(unSelected);
        } else if (LastClickStatus.equals(Code.Completed)) {
            bt_untreated.setTextColor(unSelected);
            bt_processing.setTextColor(unSelected);
            bt_completed.setTextColor(selected);
        }
    }

    private void InitialAction() {
        if (mGBGCM.getRegistrationId().isEmpty()) {
            mGBGCM.setMagicLenGCMListener(this);
            mGBGCM.openGCM();
        } else {
            Log.i("GCM", "Exist RegID : " + mGBGCM.getRegistrationId());
            RegisterGCMTask(mGBGCM.getRegistrationId());
        }
        //GCM Refresh
        mRefreshReceiver.setOnrefreshListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Refresh");
        registerReceiver(mRefreshReceiver, intentFilter);
        bt_untreated.setOnClickListener(this);
        bt_processing.setOnClickListener(this);
        bt_completed.setOnClickListener(this);
        bt_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Hardware.closeKeyBoard(ctxt, v);
                Intent iPickPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iPickPicture, PICK_PICTURE);
            }
        });
        // ListView setting
        lv_problems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProblemRecord pr = problemlist.get(position);
                Intent it = new Intent(ctxt, Act_Responses.class);
                it.putExtra("PRSNo", pr.getPRSNo());
                it.putExtra("FLaborNo", pr.getFLaborNo());
                it.putExtra("CustomerNo", pr.getCustomerNo());
                startActivityForResult(it, ActResponse);
            }
        });
        lv_problems.setAdapter(list_adapter);
        // ExpandableListView setting
        elv_workers.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                List<ProblemRecord> list = list_workers.get(groupPosition).getItems(); // this worker problems
                ProblemRecord pr = list.get(childPosition);
                Intent it = new Intent(ctxt, Act_Responses.class);
                it.putExtra("PRSNo", pr.getPRSNo());
                it.putExtra("FLaborNo", pr.getFLaborNo());
                it.putExtra("CustomerNo", pr.getCustomerNo());
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
        bt_image = (ImageView) findViewById(R.id.bt_main_image);
        lv_problems = (ListView) findViewById(R.id.lv_problems);
        elv_workers = (ExpandableListView) findViewById(R.id.elv_workers);
    }

    private void InitialSomething() {
        user = UserAccount.getUserAccount();
        con = new HttpConnection();
        problemlist = new ArrayList<>();
        responselist = new ArrayList<>();
        list_workers = new ArrayList<>();
        list_adapter = new ProblemListAdapter(ctxt, problemlist);
        exlist_adapter = new ExpandListAdapter(ctxt, list_workers);

        mGBGCM = new GoldBrotherGCM(this);
        mRefreshReceiver = new RefreshReceiver();
    }

    protected void doCropPhoto(Uri data) {
        try {
            //進行照片裁剪
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(data, "image/*");
//        intent.setType("image/*");
//        intent.putExtra("data", data);
            intent.putExtra("crop", "true");// crop=true 有這句才能叫出裁剪頁面.
            intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
            intent.putExtra("aspectY", 1);// x:y=1:1
            intent.putExtra("outputX", 300);//回傳照片比例X
            intent.putExtra("outputY", 300);//回傳照片比例Y
            intent.putExtra("return-data", true);
            startActivityForResult(intent, TRIM_PICTURE);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(ctxt, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ActResponse:
                    LoadingProblem(LastClickStatus);
                    break;
                case PICK_PICTURE:
                    Uri uri = data.getData();
                    doCropPhoto(uri);
                    break;
                case TRIM_PICTURE:
                    final Bitmap result = data.getParcelableExtra("data");

                    ImageView iv = new ImageView(ctxt);
                    iv.setImageBitmap(result);
                    new AlertDialog.Builder(ctxt, AlertDialog.THEME_HOLO_LIGHT).setView(iv).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UploadPhoto(result);
                        }
                    }).setNegativeButton("Cancel", null).show();
                    break;
            }
        }
    }

    private long lastpresstime = 0;


    @Override
    public void setRefresh(String text) {
        //Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
        LoadingProblem(LastClickStatus);
    }

    public void onDestroy() {
        unregisterReceiver(mRefreshReceiver);
        super.onDestroy();
    }

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
