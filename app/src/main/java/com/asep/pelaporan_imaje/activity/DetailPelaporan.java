package com.asep.pelaporan_imaje.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.config.DateFormat;
import com.asep.pelaporan_imaje.server.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ng.max.slideview.SlideView;

public class DetailPelaporan extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    String[] namabulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    String id_intent,hlm_id,lk_id,lk_tgl,lk_ket,lk_status,mp_nama,mu_nama,tgl_proses;
    EditText et_tgl;
    TextView tx_judulPilihTgl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pelaporan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_detailpelaporan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        TextView tx_isi =(TextView)findViewById(R.id.tx_isi_detailpelaporan);
        et_tgl =(EditText)findViewById(R.id.et_pilihtgl_detailpelaporan);
        SlideView sv_terima =(SlideView)findViewById(R.id.sv_terima_detailpelaporan);
        tx_judulPilihTgl =(TextView)findViewById(R.id.tx_judulPilihTgl_detailPel);
        ImageView logo =(ImageView)findViewById(R.id.logo_detaiPel);
        Button bt_proses =(Button)findViewById(R.id.bt_proses_detailpelaporan);

        id_intent = getIntent().getStringExtra("id_intent");
        if (id_intent.equals("Pending")){
            bt_proses.setVisibility(View.GONE);
        }else if (id_intent.equals("Proses")){
            et_tgl.setVisibility(View.GONE);
            sv_terima.setVisibility(View.GONE);
            logo.setVisibility(View.GONE);
            tx_judulPilihTgl.setVisibility(View.GONE);
        }else if (id_intent.equals("Selesai")){
            bt_proses.setVisibility(View.GONE);
            et_tgl.setVisibility(View.GONE);
            sv_terima.setVisibility(View.GONE);
            logo.setVisibility(View.GONE);
            tx_judulPilihTgl.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString("mu_flag","").equals("0")){
            et_tgl.setVisibility(View.GONE);
            sv_terima.setVisibility(View.GONE);
            logo.setVisibility(View.GONE);
            tx_judulPilihTgl.setVisibility(View.GONE);
            bt_proses.setVisibility(View.GONE);
        }

        hlm_id  = getIntent().getStringExtra("hlm_id");
        lk_id   = getIntent().getStringExtra("lk_id");
        lk_tgl  = getIntent().getStringExtra("lk_tgl");
        lk_ket   = getIntent().getStringExtra("lk_ket");
        lk_status  = getIntent().getStringExtra("lk_status");
        mp_nama = getIntent().getStringExtra("mp_nama");
        mu_nama = getIntent().getStringExtra("mu_nama");

        String isi ="Tanggal Laporan \t: "+ DateFormat.dateTimeTanggal(lk_tgl)+"\n\n" +
                "Pelapor \t: "+mu_nama+"\n\n" +
                "Perusahaan \t: "+mp_nama+"\n\n" +
                "Keterangan \t: "+lk_ket+"\n\n" +
                "Status \t: "+lk_status;

        tx_isi.setText(isi);

        et_tgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year    = calendar.get(Calendar.YEAR);
                int month   = calendar.get(Calendar.MONTH);
                int day     = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(DetailPelaporan.this,onDateSetListener,
                        year,month,day);
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = day+" "+namabulan[month]+" "+year;
                tgl_proses = year+"-"+month+"-"+day;
                et_tgl.setText(date);
                tx_judulPilihTgl.setTextColor(getResources().getColor(R.color.colorTextList));
            }
        };

        sv_terima.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                // vibrate the device
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                // go to a new activity
                if (et_tgl.getText().toString().trim().length()>0){
                    updatePel();
                }else{
                    Toast.makeText(getApplicationContext(),"Mohon isi tanggal pengerjaan",Toast.LENGTH_LONG).show();
                }
            }
        });

        bt_proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailPelaporan.this,BuatLaporanPengerjaan.class);
                intent.putExtra("id_intent","Pelaporan_proses");
                intent.putExtra("hlm_id",hlm_id);
                intent.putExtra("mp_nama",mp_nama);
                intent.putExtra("id_pergantian","0");
                startActivity(intent);
            }
        });
    }

    private void updatePel(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.URL + "buat_laporan/change_status_proses.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DetailPelaporan.this,Home.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Update Pelaporan gagal!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Response :",error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lk_id", lk_id );
                params.put("lk_status", "Proses");
                params.put("lk_update", tgl_proses);
                params.put("lk_ket", lk_ket+"\n \nTanggal pengerjaan : "+et_tgl.getText());
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
