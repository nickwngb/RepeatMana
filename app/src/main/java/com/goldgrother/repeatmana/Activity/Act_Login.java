package com.goldgrother.repeatmana.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
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
import java.util.List;

public class Act_Login extends AppCompatActivity {

    private Context ctxt = Act_Login.this;
    private HttpConnection con;
    public static UserAccount user;
    // UI
    private EditText et_account, et_password;
    private Button bt_login;
    //
    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String accountField = "ACCOUNT";
    private static final String passwordField = "PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        InitialSomething();
        InitialUI();
        InitialAction();
        readData();
    }

    private void LoginTask() {
        if (Uti.isNetWork(ctxt)) {
            new LoginTask().execute();
        } else {

        }
    }

    private class LoginTask extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog mDialog;
        private String account, password;

        protected void onPreExecute() {
            account = et_account.getText().toString();
            password = et_password.getText().toString();
            mDialog = new ProgressDialog(ctxt);
            mDialog.setMessage("Loading");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = Code.NoResponse;
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("account", account));
                postFields.add(new BasicNameValuePair("password", password));
                JSONObject jobj = con.PostGetJson(URLs.url_login, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
                        JSONArray array = jobj.getJSONArray("result");
                        JSONObject minfo = array.getJSONObject(0);
                        user.setUserID(minfo.getString("UserID"));
                        user.setUserPWD(minfo.getString("UserPWD"));
                        user.setDormID(minfo.getString("DormID"));
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
            Log.i("LoginTask", "Result:" + result);
            switch (result) {
                case Code.Success:
                    saveData();
                    Intent i = new Intent(ctxt, Act_MainScreen.class);
                    startActivity(i);
                    finish();
                    break;
                case Code.NoResponse:
                    Toast.makeText(ctxt, "Server no response", Toast.LENGTH_SHORT).show();
                    break;
                case Code.Empty:
                    Toast.makeText(ctxt, "Account or Passowrd is not exist", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isVaild() {
        String acc = et_account.getText().toString();
        String pwd = et_password.getText().toString();
        if (acc.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(ctxt, "Can't input empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void InitialAction() {
        bt_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isVaild())
                    LoginTask();
            }
        });
    }

    private void InitialUI() {
        et_account = (EditText) findViewById(R.id.et_login_account);
        et_password = (EditText) findViewById(R.id.et_login_password);
        bt_login = (Button) findViewById(R.id.bt_login_login);
    }

    private void InitialSomething() {
        user = new UserAccount();
        con = new HttpConnection();
    }

    public void readData() {
        settings = getSharedPreferences(data, 0);
        et_account.setText(settings.getString(accountField, ""));
        et_password.setText(settings.getString(passwordField, ""));
        String acc = et_account.getText().toString();
        String pwd = et_password.getText().toString();
        if (!acc.isEmpty() && !pwd.isEmpty())
            LoginTask();
    }

    public void saveData() {
        settings = getSharedPreferences(data, 0);
        settings.edit()
                .putString(accountField, et_account.getText().toString())
                .putString(passwordField, et_password.getText().toString())
                .commit();
    }
}
