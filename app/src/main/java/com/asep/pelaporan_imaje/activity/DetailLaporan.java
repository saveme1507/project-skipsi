package com.asep.pelaporan_imaje.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.config.BitmapFormat;
import com.asep.pelaporan_imaje.server.Server;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailLaporan extends AppCompatActivity {
    TextView tx_hlmID,tx_header,tx_mesin,tx_parameter,tx_ket;
    String hlm_id,header,mesin,parameter,keterangan,flag;
    SignaturePad signaturePad;
    Button bt_simpan,bt_ulangi;
    Bitmap bmp_ttd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laporan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_detailLaporan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        tx_hlmID    =(TextView)findViewById(R.id.tx_hlm_id_detailLap);
        tx_header   =(TextView)findViewById(R.id.tx_header_detailLap);
        tx_mesin    =(TextView)findViewById(R.id.tx_mesin_detailLap);
        tx_parameter=(TextView)findViewById(R.id.tx_parameter_detailLap);
        tx_ket      =(TextView)findViewById(R.id.tx_keterangan_detailLap);
        bt_simpan   =(Button)findViewById(R.id.bt_simpan_detailLap);
        bt_ulangi   =(Button)findViewById(R.id.bt_ulangi_detailLap);
        signaturePad=(SignaturePad)findViewById(R.id.signaturePad);
        RelativeLayout rl_signature =(RelativeLayout)findViewById(R.id.rl_signature);

        String hlmId = getIntent().getStringExtra("hlm_id");
        flag = getIntent().getStringExtra("flag");
        getDataLapMesin(hlmId);

        if (!flag.equals("acc")){
            rl_signature.setVisibility(View.GONE);
        }

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //event on touch to signanautre
            }

            @Override
            public void onSigned() {
                bt_simpan.setEnabled(true);
                bt_ulangi.setEnabled(true);
            }

            @Override
            public void onClear() {
                bt_simpan.setEnabled(false);
                bt_ulangi.setEnabled(false);
            }
        });
        bt_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bmp_ttd = signaturePad.getSignatureBitmap();
                simpanTtd();
            }
        });
        bt_ulangi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear();
            }
        });
    }

    private void getDataLapMesin(String hlmId){
        StringRequest stringRequest = new StringRequest( Server.URL+"buat_lapPengerjaan/select_lapMesin.php?hlm_id="+hlmId,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ",response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success") == 1) {
                        hlm_id  = jObj.getString("hlm_id");
                        header  = jObj.getString("header");
                        mesin   = jObj.getString("mesin");
                        parameter = jObj.getString("parameter");
                        keterangan = jObj.getString("keterangan");
                    }
                    tx_hlmID.setText(hlm_id);
                    tx_header.setText(header.replace("*","\n\n"));
                    tx_mesin.setText(mesin.replace("*","\n\n"));
                    tx_parameter.setText(parameter.replace("*","\n\n"));
                    tx_parameter.setText(keterangan);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Edit profil gagal", Toast.LENGTH_SHORT).show();
                }
            }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error response :", error.getMessage());
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void simpanTtd() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Server.URL+"buat_lapPengerjaan/simpanTtd.php",new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ",response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success") == 1) {
                        Toast.makeText(getApplicationContext(),jObj.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DetailLaporan.this,Home.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT).show();
                }
            }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error response :", error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hlm_ttd", BitmapFormat.getStringImage(bmp_ttd));
                params.put("hlm_id", hlm_id);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
