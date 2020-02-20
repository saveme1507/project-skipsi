package com.asep.pelaporan_imaje.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.InstanceIdResult;

public class FirebaseInstanceService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        sendNewTokenToServer(FirebaseInstanceId.getInstance().getToken());
    }

    private void sendNewTokenToServer(String token){
        Log.d("TOKEN ",String.valueOf(token));
    }
    public String getToken_1(){
        Log.d("TOKEN_1 ",String.valueOf(FirebaseInstanceId.getInstance().getToken()));
        return FirebaseInstanceId.getInstance().getToken();
    }

    public void getToken_2(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKENn", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("TOKEN_2", token);
                    }
                });
    }

}
