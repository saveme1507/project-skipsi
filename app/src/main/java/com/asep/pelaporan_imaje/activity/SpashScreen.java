package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.server.Server;
import com.asep.pelaporan_imaje.service.FirebaseInstanceService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SpashScreen extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        Thread splash = new Thread(){
            public void run(){
                try{
                    sleep(3*1000);
                        Intent i = new Intent(SpashScreen.this, Login.class);
                        startActivity(i);
                        finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splash.start();
    }
    private void cekToken(){
        FirebaseInstanceService firebaseInstanceService = new FirebaseInstanceService();
        final String token = firebaseInstanceService.getToken_1();
        final String id = sharedPreferences.getString("mu_id","");

        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"ss/cekToken.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e( "Login Response: " , response);
                Boolean session;
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")== 1){
                        session=true;
                    }else {
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean("session_status",false);
                        editor.commit();
                        session=false;
                    }
                    Intent i = new Intent(SpashScreen.this, Login.class);
                    i.putExtra("session",session);
                    startActivity(i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent i = new Intent(SpashScreen.this, Login.class);
                    i.putExtra("session",false);
                    startActivity(i);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error: " , error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mu_id",id);
                params.put("mu_token", token);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
}
