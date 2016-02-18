package com.goldgrother.repeatmana.Other;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by v on 2016/2/17.
 */
public class FreeDialog {
    public static ProgressDialog getProgressDialog(Context ctxt, String message) {
        ProgressDialog pDialog = new ProgressDialog(ctxt);
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        return pDialog;
    }
}
