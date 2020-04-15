package com.asep.pelaporan_imaje.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BuatLaporanPengerjaan extends AppCompatActivity {
    DatePickerDialog.OnDateSetListener onDateSetListener;
    Spinner sp_pengerjaan,sp_pelanggan,sp_sn;
    EditText et_tanggal;
    String[] namabulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    String[] pengerjaan = {"Perbaikan","Kunjungan Rutin","Pergantian Sparepart","Preventive Maintenance"};
    List<String> array_mp_id  ;
    List<String> array_mp_nama;
    List<String> array_mm_sn  ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_laporan_pengerjaan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_buatlaporanpengerjaan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        et_tanggal      =(EditText)findViewById(R.id.et_tanggal_lapPengerjaan);
        sp_pengerjaan   = (Spinner) findViewById(R.id.sp_pengerjaan_lapPengerjaan);
        sp_pelanggan    =(Spinner)findViewById(R.id.sp_pelanggan_lapPengerjaan);
        sp_sn           =(Spinner)findViewById(R.id.sp_sn_lapPengerjaan);
        dataSpinnerPelanggan();

        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item ,pengerjaan );
        langAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_pengerjaan.setAdapter(langAdapter);
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
                et_tanggal.setText(date);
            }
        };
    }

    private void dataSpinnerPelanggan(){
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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(BuatLaporanPengerjaan.this,android.R.layout.simple_spinner_item ,array_mp_nama );
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                sp_pelanggan.setAdapter(arrayAdapter);
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
                ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(BuatLaporanPengerjaan.this,android.R.layout.simple_spinner_item ,array_mm_sn );
                langAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                sp_sn.setAdapter(langAdapter);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

}
