package com.eryushion.beachsidebuggies.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eryushion.beachsidebuggies.R;
import com.eryushion.beachsidebuggies.activity.MapsActivitys;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by eryushion1 on 29/12/16.
 */
public class FCMMessagingService extends FirebaseMessagingService {
    String TAG = "FCMToken";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "getMessageId: " + remoteMessage.getMessageId());
        Log.d(TAG, "NotificationData: " + remoteMessage.getData());
        Log.d(TAG, "NotificationBody: " + String.valueOf(remoteMessage.getNotification().getBody()));
        Log.d(TAG, "NotificationTitle: " + String.valueOf(remoteMessage.getNotification().getTitle()));


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, MapsActivitys.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(alarmSound)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(100, notificationBuilder.build());


    }

}
