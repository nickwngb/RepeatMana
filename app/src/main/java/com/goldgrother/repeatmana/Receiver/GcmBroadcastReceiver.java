package com.goldgrother.repeatmana.Receiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.goldgrother.repeatmana.Activity.Act_Login;
import com.goldgrother.repeatmana.GcmForGB.GoldBrotherGCM;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                Log.i(getClass() + " GCM ERROR", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                Log.i(getClass() + " GCM DELETE", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                Log.i(getClass() + " GCM MESSAGE", extras.toString());
                // if click will open Login (if not in app)
                Intent i = new Intent(context, Act_Login.class);
                i.setAction("android.intent.action.MAIN");
                i.addCategory("android.intent.category.LAUNCHER");
                // send a Notification
                GoldBrotherGCM.sendLocalNotification(context, NOTIFICATION_ID, extras.getString("message"),
                        PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT));
                // push a intent to refresh reveiver
                Intent refresh_intent = new Intent();
                // name = 'Refresh'
                refresh_intent.setAction("Refresh");
                // datas
                refresh_intent.putExtra("message", extras.getString("message"));
                // send
                context.sendBroadcast(refresh_intent);
            }
        }
        setResultCode(Activity.RESULT_OK);
    }
}