package com.goldgrother.repeatmana.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.goldgrother.repeatmana.Adapter.MyListAdapter;
import com.goldgrother.repeatmana.Asyn.LoadAllLastestResponse;
import com.goldgrother.repeatmana.Asyn.LoadProblem;
import com.goldgrother.repeatmana.Asyn.UploadPhoto;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.FreeDialog;
import com.goldgrother.repeatmana.Other.Hardware;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.ProblemResponse;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.Other.Worker;
import com.goldgrother.repeatmana.R;

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
    private static final int ActProblem = 0;
    private static final int PICK_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int TRIM_PICTURE = 3;
    // UI
    private Button bt_untreated, bt_processing, bt_completed, bt_image;
    private ListView lv_problems;
    private ExpandableListView elv_workers;
    // Adapter
    private MyListAdapter list_adapter;
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
                            LoadAllLastestResponse(status);
                            break;
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

    private void LoadAllLastestResponse(final String status) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog pd = FreeDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllLastestResponse task = new LoadAllLastestResponse(con, new LoadAllLastestResponse.OnLoadAllResponseListener() {
                public void finish(Integer result, List<ProblemResponse> list) {
                    pd.dismiss();
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

    private void UploadPhoto(String photo) {
        if (Uti.isNetWork(ctxt)) {
            final ProgressDialog fd = FreeDialog.getProgressDialog(ctxt, "Loading...");
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
            });
            task.execute(user.getUserID(), photo);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
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
                startActivityForResult(it, ActProblem);
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
        bt_image = (Button) findViewById(R.id.bt_main_image);
        lv_problems = (ListView) findViewById(R.id.lv_problems);
        elv_workers = (ExpandableListView) findViewById(R.id.elv_workers);
    }

    private void InitialSomething() {
        user = UserAccount.getUserAccount();
        con = new HttpConnection();
        problemlist = new ArrayList<>();
        responselist = new ArrayList<>();
        list_workers = new ArrayList<>();
        list_adapter = new MyListAdapter(ctxt, problemlist);
        exlist_adapter = new ExpandListAdapter(ctxt, list_workers);
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
                case ActProblem:
                    LoadingProblem(LastClickStatus);
                    break;
                case PICK_PICTURE:
                    Uri uri = data.getData();
                    doCropPhoto(uri);
                    break;
                case TAKE_PICTURE:
                    Bitmap iBitmap = (Bitmap) data.getExtras().get("data");
                    break;
                case TRIM_PICTURE:
                    final Bitmap result = data.getParcelableExtra("data");

                    ImageView iv = new ImageView(ctxt);
                    iv.setImageBitmap(result);
                    new AlertDialog.Builder(ctxt, AlertDialog.THEME_HOLO_LIGHT).setView(iv).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //UploadPhoto(BitmapTransformer.BitmapToBase64(result));
                        }
                    }).setNegativeButton("Cancel", null).show();
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
