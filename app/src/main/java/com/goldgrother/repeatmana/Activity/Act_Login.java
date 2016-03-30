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

import com.goldgrother.repeatmana.Asyn.Login;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.FreeDialog;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.URLs;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.Other.Vaild;
import com.goldgrother.repeatmana.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Act_Login extends AppCompatActivity {

    private Context ctxt = Act_Login.this;
    private HttpConnection con;
    private UserAccount user;
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
            final ProgressDialog pd = FreeDialog.getProgressDialog(ctxt, "Loading...");
            Login task = new Login(con, new Login.OnLoginListener() {
                public void finish(Integer result, String account, String password, String UserPhoto, String DormID) {
                    pd.dismiss();
                    Log.i("LoginTask", "Result : " + result);
                    switch (result) {
                        case Code.Success:
                            user.setUserID(account);
                            user.setUserPWD(password);
                            user.setDormID(DormID);
                            user.setPhoto(UserPhoto);
                            saveData();
                            Intent i = new Intent(ctxt, Act_MainScreen.class);
                            startActivity(i);
                            finishActivity();
                            break;
                        case Code.NoResponse:
                            Toast.makeText(ctxt, "Server No Response", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.Empty:
                            Toast.makeText(ctxt, "Account or Passowrd is not exist", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute(et_account.getText().toString(), et_password.getText().toString());
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void InitialAction() {
        bt_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String acc = et_account.getText().toString();
                String pwd = et_password.getText().toString();
                if (Vaild.login(ctxt, acc, pwd)) {
                    LoginTask();
                }

            }
        });
    }

    private void InitialUI() {
        et_account = (EditText) findViewById(R.id.et_login_account);
        et_password = (EditText) findViewById(R.id.et_login_password);
        bt_login = (Button) findViewById(R.id.bt_login_login);
    }

    private void InitialSomething() {
        user = UserAccount.getUserAccount();
        con = new HttpConnection();
    }

    public void readData() {
        settings = getSharedPreferences(data, 0);
        et_account.setText(settings.getString(accountField, ""));
        et_password.setText(settings.getString(passwordField, ""));
        String acc = et_account.getText().toString();
        String pwd = et_password.getText().toString();
        if (acc.isEmpty() || pwd.isEmpty()) return;
        LoginTask();
    }

    public void saveData() {
        settings = getSharedPreferences(data, 0);
        settings.edit()
                .putString(accountField, et_account.getText().toString())
                .putString(passwordField, et_password.getText().toString())
                .commit();
    }

    private void finishActivity() {
        this.finish();
    }

    public String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
