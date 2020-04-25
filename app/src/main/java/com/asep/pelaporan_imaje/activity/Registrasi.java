package com.asep.pelaporan_imaje.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.server.Server;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Registrasi extends AppCompatActivity {
    NiceSpinner niceSpinner;
    ProgressDialog pDialog;
    Button btn_register, btn_login;
    EditText txt_username, txt_password, txt_confirm_password, txt_email;
    Intent intent;
    ArrayList<String> pt = new ArrayList<String>();
    int success;
    ConnectivityManager conMgr;

    private static final String TAG = Registrasi.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        namaPT();

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        btn_login = (Button) findViewById(R.id.daftar_login);
        btn_register = (Button) findViewById(R.id.daftar_daftar);
        txt_username = (EditText) findViewById(R.id.et_user_reg);
        txt_password = (EditText) findViewById(R.id.et_pass_reg);
        txt_confirm_password = (EditText) findViewById(R.id.et_konfirmasiPass_reg);
        txt_email   = (EditText) findViewById(R.id.et_email_reg);
        niceSpinner = (NiceSpinner) findViewById(R.id.daftar_spinnerCust);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Registrasi.this, Login.class);
                finish();
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = txt_username.getText().toString();
                String email    = txt_email.getText().toString();
                String password = txt_password.getText().toString();
                String confirm_password = txt_confirm_password.getText().toString();
                String cust = niceSpinner.getText().toString();

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    if (username.trim().length()>0 && email.trim().length()>0 && password.trim().length()>0 &&
                            confirm_password.trim().length()>0 && cust.trim().length()>0){
                        if (password.equals(confirm_password)){
                            checkRegister(username, email, password, confirm_password, cust);
                        }else {
                            Toast.makeText(getApplicationContext(),"Mohon ceh kembali password yang anda masukan", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"kolom tidak boleh kosong",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkRegister(final String username, final String email, final String password, final String confirm_password, final String cust) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Mohon Menunggu...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"login/registrasi.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Registrasi Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Berhasil Mendaftar", jObj.toString());
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_username.setText("");
                        txt_email.setText("");
                        txt_password.setText("");
                        txt_confirm_password.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                String flag;
                if (cust.equals("PT. Printech Group")){
                    flag="1";
                }else {
                    flag="0";
                }
                params.put("nama", username);
                params.put("email", email);
                params.put("password", password);
                params.put("flag",flag);
                params.put("nama_pt", cust);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
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

    @Override
    public void onBackPressed() {
        intent = new Intent(Registrasi.this, Login.class);
        finish();
        startActivity(intent);
    }

    private void namaPT(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "login/get_nama_pt.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i<response.length();i++){
                    try{
                        JSONObject jsonObject = response.getJSONObject(i);
                        pt.add(jsonObject.getString("nama_pt"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                niceSpinner.attachDataSource(pt);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
