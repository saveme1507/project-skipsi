package com.asep.pelaporan_imaje.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.config.DateFormat;
import com.asep.pelaporan_imaje.server.Server;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuatLaporan extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ArrayList<String> sn = new ArrayList<>();
    ArrayList<String> line = new ArrayList<>();
    ArrayList<String> mm_id = new ArrayList<>();
    NiceSpinner ns_line,ns_sn;
    TextView tx_mm_id;
    EditText et_deskripsi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_laporan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_buatLaporan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        tx_mm_id= (TextView)findViewById(R.id.tx_mm_id_buatLap);
        ns_line = (NiceSpinner)findViewById(R.id.ns_line_buatLap);
        ns_sn   = (NiceSpinner)findViewById(R.id.ns_sn_buatLap);
        et_deskripsi =(EditText)findViewById(R.id.et_deskripsi_buatLap);
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        dataSpinner(sharedPreferences.getString("mu_id_pt",""));

        ns_line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ns_sn.setSelectedIndex(i);
                tx_mm_id.setText(mm_id.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ns_sn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ns_line.setSelectedIndex(i);
                tx_mm_id.setText(mm_id.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button bt_kirim = (Button)findViewById(R.id.bt_kirim_buatLap);
        bt_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpan();
            }
        });
    }
    private void dataSpinner(String id_pt){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "buat_laporan/spinner_by_idpt.php?id_pt="+id_pt,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("response :",response.toString());
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        mm_id.add(jsonObject.getString("mm_id"));
                        sn.add(jsonObject.getString("mm_sn"));
                        line.add(jsonObject.getString("mm_posisi"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ns_line.attachDataSource(line);
                ns_sn.attachDataSource(sn);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void simpan(){
        final ProgressDialog loading = ProgressDialog.show(this, "Menyimpan", "Mohon Menungggu...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.URL + "buat_laporan/simpan_pelaporan.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            Log.d("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        et_deskripsi.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Laporan gagal dikirim!", Toast.LENGTH_LONG).show();
                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("Error Response :",error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mp_id", sharedPreferences.getString("mu_id_pt","") );
                params.put("mm_id", tx_mm_id.getText().toString());
                params.put("mu_id", sharedPreferences.getString("mu_id",""));
                params.put("deskripsi", et_deskripsi.getText().toString());
                params.put("tgl_lap", DateFormat.currentDatetimeFormatSQL());
                params.put("mp_nama",sharedPreferences.getString("mp_nama",""));
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

