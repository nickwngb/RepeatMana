package com.goldgrother.repeatmana.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by v on 2015/12/25.
 */
public class RefreshReceiver extends BroadcastReceiver {
    private OnrefreshListener listener;

    public interface OnrefreshListener {
        void setRefresh(String text);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("Refresh")) {
            if (listener != null) {
                if (intent != null) {
                    String message = intent.getStringExtra("message");
                    listener.setRefresh(message);
                }
            }
        }
    }

    public void setOnrefreshListener(OnrefreshListener listener) {
        this.listener = listener;
    }
}
