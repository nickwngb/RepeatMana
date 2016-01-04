package com.goldgrother.repeatmana.Other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by v on 2016/1/1.
 */
public class Uti {
    public static boolean isNetWork(Context context) {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            result = false;
        } else {
            if (!info.isAvailable()) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
    }
}
