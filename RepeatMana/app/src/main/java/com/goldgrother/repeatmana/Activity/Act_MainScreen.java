package com.goldgrother.repeatmana.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.R;

/**
 * Created by v on 2016/1/1.
 */
public class Act_MainScreen extends AppCompatActivity {

    private Context ctxt = Act_MainScreen.this;
    private HttpConnection con;
    public UserAccount user = Act_Login.user;

    private Button bt_untreated, bt_processing, bt_completed;
    private ListView lv_problems;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.act_mainscreen);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void InitialAction() {
        bt_untreated.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        bt_processing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        bt_completed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

    private void InitialUI() {
        bt_untreated = (Button) findViewById(R.id.bt_main_untreated);
        bt_processing = (Button) findViewById(R.id.bt_main_processing);
        bt_completed = (Button) findViewById(R.id.bt_main_completed);
        lv_problems = (ListView) findViewById(R.id.lv_problems);
    }

    private void InitialSomething() {
        con = new HttpConnection();
    }
}
