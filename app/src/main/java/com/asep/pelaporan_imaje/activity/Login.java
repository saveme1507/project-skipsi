package com.asep.pelaporan_imaje.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Login extends AppCompatActivity {
        ProgressDialog pDialog;
        Button btn_register, btn_login;
        EditText txt_username, txt_password;
        Intent intent;
        int success;
        ConnectivityManager conMgr;

        private static final String TAG = Login.class.getSimpleName();

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        SharedPreferences sharedpreferences;
        Boolean session = false;
        public static final String session_status = "session_status";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            tambahToken();
            conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            {
                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi", Toast.LENGTH_LONG).show();
                }
            }

            btn_login = (Button) findViewById(R.id.li_masuk);
            btn_register = (Button) findViewById(R.id.li_daftar);
            txt_username = (EditText) findViewById(R.id.li_username);
            txt_password = (EditText) findViewById(R.id.li_password);

            // Cek session login jika TRUE maka langsung buka MainLogin
            sharedpreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
            session = sharedpreferences.getBoolean(session_status, false);

            if (session) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                finish();
                startActivity(intent);
            }


            btn_login.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String username = txt_username.getText().toString().toLowerCase();
                    String password = txt_password.getText().toString();

                    // mengecek kolom yang kosong
                    if (username.trim().length() > 0 && password.trim().length() > 0) {
                        if (conMgr.getActiveNetworkInfo() != null
                                && conMgr.getActiveNetworkInfo().isAvailable()
                                && conMgr.getActiveNetworkInfo().isConnected()) {
                            checkLogin(username, password);
                        } else {
                            Toast.makeText(getApplicationContext() ,"Tidak Ada Koneksi internet", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Prompt user to enter credentials
                        Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                    }
                }
            });

            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Login.this, Registrasi.class);
                    finish();
                    startActivity(intent);
                }
            });

        }

        //proses Login
        private void checkLogin(final String username, final String password) {
            pDialog = new ProgressDialog(this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"login/login.php", new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "Login Response: " + response.toString());
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        success = jObj.getInt(TAG_SUCCESS);

                        // Check for error node in json
                        if (success == 1) {
                            String a = jObj.getString("id_usr");
                            String b = jObj.getString("nama_usr");
                            String c = jObj.getString("telp_usr");
                            String d = jObj.getString("email_usr");
                            String e = jObj.getString("id_pt");
                            String f = jObj.getString("logo_usr");
                            String g = jObj.getString("flag_usr");
                            String h = jObj.getString("nama_pt");

                            Log.e("Anda berhasil Login", jObj.toString());
                            Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                            // menyimpan login ke session
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean(session_status, true);
                            editor.putString("id_usr", a);
                            editor.putString("nama_usr", b);
                            editor.putString("telp_usr", c);
                            editor.putString("email_usr", d);
                            editor.putString("id_pt", e);
                            editor.putString("logo_usr", f);
                            editor.putString("flsg_usr", g);
                            editor.putString("nama_pt", h);

                            editor.commit();

                            // Memanggil main activity
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Koneksi Database Error", Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nama/email", username);
                    params.put("password", password);

                    return params;
                }

            };

            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(strReq);
        }

        private void showDialog() {
            if (!pDialog.isShowing())
                pDialog.show();
        }

        private void hideDialog() {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

        private void tambahToken(){
            FirebaseInstanceService firebaseInstanceService = new FirebaseInstanceService();
            final String token = firebaseInstanceService.getToken_1();
            sharedpreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
            final String id = sharedpreferences.getString("id_usr","null");

            StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"login/tambah_token.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "Login Response: " + response.toString());
                    try {
                        JSONObject jObj = new JSONObject(response);
                        Log.e("Successfully Login!", jObj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_usr",id);
                    params.put("token", token);
                    return params;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(strReq);
        }
}