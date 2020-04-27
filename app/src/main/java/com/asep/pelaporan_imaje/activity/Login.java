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
import android.widget.TextView;
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

import in.anshul.libray.PasswordEditText;

public class Login extends AppCompatActivity {
        ProgressDialog pDialog;
        Button  btn_login;
        PasswordEditText et_pass;
        EditText et_user;
        TextView register;
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
            register = (TextView) findViewById(R.id.li_daftar);
            et_user     =(EditText)findViewById(R.id.et_user_login);
            et_pass     =(PasswordEditText)findViewById(R.id.et_pass_login);

            // Cek session login jika TRUE maka langsung buka MainLogin
            sharedpreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
            session = sharedpreferences.getBoolean(session_status, false);

            if (session) {
                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
                finish();
            }

            btn_login.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String username = et_user.getText().toString().toLowerCase();
                    String password = et_pass.getText().toString();

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

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Login.this, Registrasi.class);
                    finish();
                    startActivity(intent);
                }
            });

        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Login.this.finish();
        System.exit(0);
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
                            String a = jObj.getString("mu_id");
                            String b = jObj.getString("mu_nama");
                            String c = jObj.getString("mu_telp");
                            String d = jObj.getString("mu_email");
                            String e = jObj.getString("mu_pass");
                            String f = jObj.getString("mu_logo");
                            String g = jObj.getString("mu_token");
                            String h = jObj.getString("mu_id_pt");
                            String i = jObj.getString("mp_nama");
                            String j = jObj.getString("mu_flag");

                            Log.e("Anda berhasil Login", jObj.toString());
                            Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                            // menyimpan login ke session
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean(session_status, true);
                            editor.putString("mu_id", a);
                            editor.putString("mu_nama", b);
                            editor.putString("mu_telp", c);
                            editor.putString("mu_email", d);
                            editor.putString("mu_pass", e);
                            editor.putString("mu_logo", f);
                            editor.putString("mu_token", g);
                            editor.putString("mu_id_pt", h);
                            editor.putString("mp_nama",i);
                            editor.putString("mu_flag",j);
                            editor.commit();

                            // Memanggil main activity
                            Intent intent = new Intent(Login.this, Home.class);
                            finish();
                            startActivity(intent);
                            Log.d("pass benar", String.valueOf(jObj.getInt("success")));
                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Email atau password salah!", Toast.LENGTH_SHORT).show();
                        Log.d("response erorr :", "Email atau password salah!");
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
            final String id = sharedpreferences.getString("mu_id","null");

            StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"login/tambah_token.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "Login Response: " + response.toString());
                    try {
                        JSONObject jObj = new JSONObject(response);
                        Log.e("Tambah TOKEN succes", jObj.toString());
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
                    params.put("mu_id",id);
                    params.put("mu_token", token);
                    return params;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(strReq);
        }
}