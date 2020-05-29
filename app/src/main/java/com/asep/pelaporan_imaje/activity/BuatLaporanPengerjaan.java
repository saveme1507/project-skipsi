package com.asep.pelaporan_imaje.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.asep.pelaporan_imaje.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuatLaporanPengerjaan extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    Spinner sp_pengerjaan,sp_pelanggan,sp_sn;
    EditText et_tanggal,et_vm,et_vj,et_press,et_visco,et_temp,et_desk;
    String[] namabulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    String[] pengerjaan = {"Perbaikan","Kunjungan Rutin","Preventive Maintenance"};
    List<String> array_mp_id  ;
    List<String> array_mp_nama;
    List<String> array_mm_sn  ;
    String id_intent,hlm_id,mp_nama,tanggal,id_pergantian;
    ArrayAdapter<String> snAdapter;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_laporan_pengerjaan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_buatlaporanpengerjaan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);

        et_tanggal      =(EditText)findViewById(R.id.et_tanggal_lapPengerjaan);
        sp_pengerjaan   = (Spinner) findViewById(R.id.sp_pengerjaan_lapPengerjaan);
        sp_pelanggan    =(Spinner)findViewById(R.id.sp_pelanggan_lapPengerjaan);
        sp_sn           =(Spinner)findViewById(R.id.sp_sn_lapPengerjaan);
        et_vm       =(EditText)findViewById(R.id.et_vm_lapPengerjaan);
        et_vj       =(EditText)findViewById(R.id.et_vj_lapPengerjaan);
        et_press    =(EditText)findViewById(R.id.et_press_lapPengerjaan);
        et_visco    =(EditText)findViewById(R.id.et_visco_lapPengerjaan);
        et_temp     =(EditText)findViewById(R.id.et_temp_lapPengerjaan);
        et_desk     =(EditText)findViewById(R.id.et_deskripsi_lapPengerjaan);

        id_intent = getIntent().getStringExtra("id_intent");
        mp_nama = getIntent().getStringExtra("mp_nama");

        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item ,pengerjaan );
        langAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_pengerjaan.setAdapter(langAdapter);

        if (id_intent.equals("Pelaporan_proses")){
            dataSpinnerPelanggan(mp_nama);
            sp_pelanggan.setEnabled(false);
            dataSpinnerSN(mp_nama);
            sp_sn.setEnabled(false);
            hlm_id = getIntent().getStringExtra("hlm_id");
        }else if (id_intent.equals("Detail_home_lap_kunjungan")){
            dataSpinnerSN("");
            dataSpinnerPelanggan("");
            hlm_id="null";
        }

        sp_pelanggan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataSpinnerSN(sp_pelanggan.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        et_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year    = calendar.get(Calendar.YEAR);
                int month   = calendar.get(Calendar.MONTH);
                int day     = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BuatLaporanPengerjaan.this,onDateSetListener,
                        year,month,day);
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = day+" "+namabulan[month]+" "+year;
                tanggal = year+"-"+(month+1)+"-"+day;
                et_tanggal.setText(date);
                getSn(hlm_id);
            }
        };

        TextView tx_pergantianPart = findViewById(R.id.tx_pergantianPart_lapPengerjaan);
        tx_pergantianPart.setVisibility(View.GONE);
        TextView tx_optional = findViewById(R.id.tx_optional_lapPengerjaan);
        tx_optional.setVisibility(View.GONE);
//        tx_pergantianPart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(BuatLaporanPengerjaan.this,PergantianPart.class);
//                intent.putExtra("id_intent","lapMesin");
//                startActivityForResult(intent,10);
//            }
//        });
        Button bt_simpan=(Button)findViewById(R.id.bt_simpan_lapPengerjaan);
        bt_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_tanggal.getText().toString().length()>0
                        && et_vm.getText().toString().length()>0
                        && et_vj.getText().toString().length()>0
                        && et_press.getText().toString().length()>0
                        && et_visco.getText().toString().length()>0
                        && et_temp.getText().toString().length()>0
                        && et_desk.getText().toString().length()>0){
                    simpan();
                }else {
                    Toast.makeText(getApplicationContext(),"Mohon mengisi semua data",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void dataSpinnerPelanggan(final String mp_nama){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "data_mesin/select_namaPT.php",new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("response :",response.toString());
                array_mp_id = new ArrayList<>();
                array_mp_nama = new ArrayList<>();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        array_mp_id.add(jsonObject.getString("mp_id"));
                        array_mp_nama.add(jsonObject.getString("mp_nama"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                arrayAdapter = new ArrayAdapter<String>(BuatLaporanPengerjaan.this,android.R.layout.simple_spinner_item ,array_mp_nama );
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                sp_pelanggan.setAdapter(arrayAdapter);
                if (mp_nama.trim().length()>1){
                    ArrayAdapter adapter_sn= (ArrayAdapter)sp_pelanggan.getAdapter();
                    int posisi = adapter_sn.getPosition(mp_nama);
                    sp_pelanggan.setSelection(posisi);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    private void dataSpinnerSN(String namaPT){
        String url=Server.URL + "data_mesin/select_datamesin_by_namaPT.php?nama_pt="+namaPT;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url.replace(" ","%20"),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response :",response.toString());
                array_mm_sn = new ArrayList<>();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        array_mm_sn.add(jsonObject.getString("mm_sn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                snAdapter = new ArrayAdapter<String>(BuatLaporanPengerjaan.this,android.R.layout.simple_spinner_item ,array_mm_sn );
                snAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                sp_sn.setAdapter(snAdapter);

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    private void getSn(String dlm_id){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "buat_laporan/get_sn_mesin.php?dlm_id="+dlm_id,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("response :",response.toString());
                String sn = null;
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        sn = jsonObject.getString("mm_sn");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter arrayAdapter=(ArrayAdapter)sp_sn.getAdapter();
                int position = arrayAdapter.getPosition(sn);
                sp_sn.setSelection(position);
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.URL + "buat_lapPengerjaan/simpan_laporan_mesin.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BuatLaporanPengerjaan.this,Home.class);
                        startActivity(intent);
                        finishAffinity();
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
                params.put("hlm_tgl",tanggal);
                params.put("hlm_pengerjaan", sp_pengerjaan.getSelectedItem().toString());
                params.put("hlm_id_teknisi", sharedPreferences.getString("mu_id",""));
                params.put("hlm_id", hlm_id);
                params.put("dlm_vj", et_vj.getText().toString());
                params.put("dlm_vm", et_vm.getText().toString());
                params.put("dlm_press", et_press.getText().toString());
                params.put("dlm_visco", et_visco.getText().toString());
                params.put("dlm_temp", et_temp.getText().toString());
                params.put("dlm_ket", et_desk.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
