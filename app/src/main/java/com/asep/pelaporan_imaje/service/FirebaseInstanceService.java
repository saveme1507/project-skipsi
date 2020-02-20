package com.asep.pelaporan_imaje.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        sendNewTokenToServer(FirebaseInstanceId.getInstance().getToken());
    }

    private void sendNewTokenToServer(String token){
        Log.d("TOKEN ",String.valueOf(token));
    }
    public String token(){
        Log.d("TOKEN ",String.valueOf(FirebaseInstanceId.getInstance().getToken()));
        return FirebaseInstanceId.getInstance().getToken();
    }

}
