package com.goldgrother.repeatmana.Presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;

import com.goldgrother.repeatmana.Activity.Act_Login;
import com.goldgrother.repeatmana.Callback.LoginCallback;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.MyDialog;
import com.goldgrother.repeatmana.Other.URLs;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Vaild;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/4/5.
 */
public class LoginPresenter {

    private Act_Login activity;
    private LoginCallback mCallback;
    private SharedPreferences settings;

    public LoginPresenter(Context context) {
        this.activity = (Act_Login) context;
        this.mCallback = this.activity;
    }

    public void execLogin(EditText account, EditText password) {
        String acc = account.getText().toString();
        String pwd = password.getText().toString();
        if (Vaild.login(activity, acc, pwd)) {
            new LoginTask().execute(acc, pwd);
        }
    }

    private class LoginTask extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog pd;
        private String account;
        private String password;
        private String UserPhoto;
        private String DormID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = MyDialog.getProgressDialog(activity, "Loading...");
        }

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = Code.NoResponse;
            account = datas[0];
            password = datas[1];
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("account", account));
                postFields.add(new BasicNameValuePair("password", password));
                JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_login, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
                        JSONArray array = jobj.getJSONArray("result");
                        JSONObject minfo = array.getJSONObject(0);
                        UserPhoto = minfo.getString("UserPhoto");
                        DormID = minfo.getString("DormID");
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
            pd.dismiss();
            switch (result) {
                case Code.Success:
                    UserAccount user = UserAccount.getUserAccount();
                    user.setUserID(account);
                    user.setUserPWD(password);
                    user.setDormID(DormID);
                    user.setPhoto(UserPhoto);

                    mCallback.success();
                    break;
                case Code.Fail:
                    mCallback.fail();
                    break;
                case Code.NoResponse:
                    mCallback.noResponse();
                    break;
            }
        }
    }

    private static final String data = "DATA";
    private static final String accountField = "ACCOUNT";
    private static final String passwordField = "PASSWORD";

    public void saveLoginInfomations(EditText account, EditText password) {
        settings = activity.getSharedPreferences(data, 0);
        settings.edit()
                .putString(accountField, account.getText().toString())
                .putString(passwordField, password.getText().toString())
                .commit();
    }

    public void readLoginInfomations(EditText account, EditText password) {
        settings = activity.getSharedPreferences(data, 0);
        account.setText(settings.getString(accountField, ""));
        password.setText(settings.getString(passwordField, ""));
        if (account.getText().toString().isEmpty() || password.getText().toString().isEmpty())
            return;
        execLogin(account, password);
    }
}
