package com.asep.pelaporan_imaje.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.asep.pelaporan_imaje.R;

import java.util.Calendar;

import ng.max.slideview.SlideView;

public class DetailPelaporan extends AppCompatActivity {
    DatePickerDialog.OnDateSetListener onDateSetListener;
    String[] namabulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    String lk_id,lk_tgl,lk_ket,lk_status,mp_nama,mu_nama;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pelaporan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_detailpelaporan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        TextView tx_isi =(TextView)findViewById(R.id.tx_isi_detailpelaporan);
        final EditText et_tgl =(EditText)findViewById(R.id.et_pilihtgl_detailpelaporan);
        SlideView sv_terima =(SlideView)findViewById(R.id.sv_terima_detailpelaporan);

        lk_id   = getIntent().getStringExtra("lk_id");
        lk_tgl  = getIntent().getStringExtra("lk_tgl");
        lk_ket   = getIntent().getStringExtra("lk_ket");
        lk_status  = getIntent().getStringExtra("lk_status");
        mp_nama = getIntent().getStringExtra("mp_nama");
        mu_nama = getIntent().getStringExtra("mu_nama");

        String isi ="Tanggal Laporan \t: "+lk_tgl+"\n\n" +
                "Pelapor \t\t: "+mu_nama+"\n\n" +
                "Perusahaan \t: "+mp_nama+"\n\n" +
                "Keterangan \t: "+lk_ket+"\n\n" +
                "Status \t\t: "+lk_status;

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
                et_tgl.setText(date);
            }
        };

        sv_terima.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                // vibrate the device
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                // go to a new activity
                Intent intent = new Intent(DetailPelaporan.this,Pelaporan.class);
                intent.putExtra("statusLap","Proses");
                startActivity(intent);
                finish();
            }
        });
    }
}
