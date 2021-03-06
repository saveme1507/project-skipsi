package com.asep.pelaporan_imaje.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.activity.Login;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    Uri urinotification;
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

        try{
            urinotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),urinotification);
            r.play();
        }catch (Exception e){
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIF_CHANEL_ID="notif_Id";
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

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
                setSmallIcon(R.mipmap.ic_launcher_offline_round).
                setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_offline_round)).
                setTicker("asepW").
                setContentTitle(judul).
                setContentText(isi).
                setContentInfo("Info").
                setSound(urinotification).
                setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());



    }
}
