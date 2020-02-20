package com.asep.pelaporan_imaje.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData() != null){
            sendNotification(remoteMessage);
        }
    }
    private void sendNotification(RemoteMessage remoteMessage){
        Map<String,String> data=remoteMessage.getData();
        String judul = data.get("judul");
        String isi   = data.get("isi");

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIF_CHANEL_ID="notif_Id";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            //Hanya aktif di android O=API26 ke atas
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIF_CHANEL_ID,
                    "Notifikasi Pelaporan_Imaje",NotificationManager.IMPORTANCE_MAX);

            //Konfirgurasi Notif Chanel
            notificationChannel.setDescription("Notif Pelaporan_Imaje");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.green(1));
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIF_CHANEL_ID);
        builder.setAutoCancel(true).
                setDefaults(Notification.DEFAULT_ALL).
                setWhen(System.currentTimeMillis()).
                setSmallIcon(android.R.mipmap.sym_def_app_icon).
                setTicker("asepW").
                setContentTitle(judul).
                setContentText(isi).
                setContentInfo("Info");

        notificationManager.notify(1, builder.build());
    }
}
