package com.goldgrother.repeatmana.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldgrother.repeatmana.Callback.LoginCallback;
import com.goldgrother.repeatmana.Presenter.LoginPresenter;
import com.goldgrother.repeatmana.R;



public class Act_Login extends AppCompatActivity implements LoginCallback {

    private Context ctxt = Act_Login.this;
    private LoginPresenter loginPresenter;
    // UI
    private EditText et_account, et_password;
    private Button bt_login;
    //dfxzsae

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        et_account = (EditText) findViewById(R.id.et_login_account);
        et_password = (EditText) findViewById(R.id.et_login_password);
        bt_login = (Button) findViewById(R.id.bt_login_login);

        bt_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loginPresenter.execLogin(et_account,et_password);
            }
        });
        loginPresenter = new LoginPresenter(this);
        loginPresenter.readLoginInfomations(et_account,et_password);
    }


    @Override
    public void success() {
        loginPresenter.saveLoginInfomations(et_account,et_password);
        Intent i = new Intent(ctxt, Act_MainScreen.class);
        startActivity(i);
        finish();
    }

    @Override
    public void fail() {
        Toast.makeText(ctxt, "Account or Passowrd is not exist", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noResponse() {
        Toast.makeText(ctxt, "Server No Response", Toast.LENGTH_SHORT).show();
    }
}
