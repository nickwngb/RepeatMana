package com.goldgrother.repeatmana.Other;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;

/**
 * Created by v on 2016/2/17.
 */
public class MyDialog {
    public static ProgressDialog getProgressDialog(Context ctxt, String message) {
        ProgressDialog pDialog = new ProgressDialog(ctxt);
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public static AlertDialog showTextDialog(Context ctxt, String text) {
        AlertDialog.Builder b = new AlertDialog.Builder(ctxt);
        b.setMessage(text);
//        TextView tv = new TextView(ctxt);
//        tv.setTextSize(20f);
//        tv.setPadding(5, 5, 5, 5);
//        tv.setText(text);
        b.setNegativeButton("CLOSE", null);
        //b.setView(tv);
        return b.show();
    }
}
