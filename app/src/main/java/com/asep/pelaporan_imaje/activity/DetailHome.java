package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.asep.pelaporan_imaje.R;

public class DetailHome extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detailhome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);

        Button bt_datamesin = (Button)findViewById(R.id.bt_datamesin_detailhome);
        bt_datamesin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,DataMesin.class);
                intent.putExtra("id_intent","data_mesin");
                startActivity(intent);
            }
        });

        Button bt_sparepart = (Button)findViewById(R.id.br_sparepart_detailhome);
        bt_sparepart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,Sparepart.class);
                startActivity(intent);
            }
        });

        Button bt_jadwalpm = (Button)findViewById(R.id.bt_jadwalpm_detailhome);
        bt_jadwalpm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,JadwalPm.class);
                startActivity(intent);
            }
        });

        Button bt_pengaturan = (Button)findViewById(R.id.bt_pengaturan_detailhome);
        bt_pengaturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,Pengaturan.class);
                startActivity(intent);
            }
        });

        Button bt_buatlpaoran = (Button)findViewById(R.id.bt_buatlaporan_detailhome);
        bt_buatlpaoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,BuatLaporan.class);
                startActivity(intent);
            }
        });

        Button bt_kontak = (Button)findViewById(R.id.bt_kontak_detailhome);
        bt_kontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,Kontak.class);
                startActivity(intent);
            }
        });

        Button bt_laporan = (Button)findViewById(R.id.bt_laporan_detailhome);
        bt_laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,Laporan.class);
                startActivity(intent);
            }
        });
        Button bt_bantuan = (Button)findViewById(R.id.bt_bantuan_detailhome);
        bt_bantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,Bantuan.class);
                startActivity(intent);
            }
        });

        Button bt_jadwalkunjungan = (Button)findViewById(R.id.bt_jadwalkunjungan_detailhome);
        bt_jadwalkunjungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,JadwalKunjungan.class);
                startActivity(intent);
            }
        });

        Button bt_pengaturanDatamesin =(Button)findViewById(R.id.bt_pengaturanDatamesin_detailHome);
        bt_pengaturanDatamesin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,DataMesin.class);
                intent.putExtra("id_intent","pengaturan_data_mesin");
                startActivity(intent);
            }
        });

        Button bt_tambahPelanggan =(Button)findViewById(R.id.bt_tambahpelanggan_detailhome);
        bt_tambahPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,Pelanggan.class);
                startActivity(intent);
            }
        });
        Button bt_buatLapKunjungan = (Button)findViewById(R.id.bt_buatlaporankunjungan_detailhome);
        bt_buatLapKunjungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,BuatLaporanPengerjaan.class);
                startActivity(intent);
            }
        });
        Button bt_pergantianpart  =  (Button)findViewById(R.id.bt_pergantianpart_detailhome);
        bt_pergantianpart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,PergantianPart.class);
                startActivity(intent);
            }
        });
        Button bt_lapPm = (Button)findViewById(R.id.bt_buatlaporanpm_detailhome);
        bt_lapPm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailHome.this,BuatLaporanPengerjaan.class);
                startActivity(intent);
            }
        });
        if (sharedPreferences.getString("mu_flag","").equals("0")){
            bt_jadwalkunjungan.setVisibility(View.GONE);
            bt_pengaturanDatamesin.setVisibility(View.GONE);
            bt_tambahPelanggan.setVisibility(View.GONE);
            bt_buatLapKunjungan.setVisibility(View.GONE);
            bt_pergantianpart.setVisibility(View.GONE);
            bt_lapPm.setVisibility(View.GONE);
        }
    }
}
