package com.asep.pelaporan_imaje.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class Home extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    PhotoViewAttacher photoViewAttacher;
    TextView nama,pt,tx_notif_pending,tx_notif_proses,tx_senin1,tx_senin2,tx_selasa1,tx_selasa2,tx_rabu1,tx_rabu2,tx_kamis1,tx_kamis2,tx_jumat1,tx_jumat2;
    ImageView foto_profil;
    HashMap<String, String> data_slide ;
    String mp_nama,proses,pending;
    ArrayList<Home.ItemJadwalKunjungan> itemJadwalKunjungans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tollbarHome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        nama = (TextView) findViewById(R.id.home_namaUsr);
        pt   = (TextView) findViewById(R.id.home_namaPT);
        foto_profil = (ImageView) findViewById(R.id.home_logoUsr);
        tx_notif_pending = (TextView)findViewById(R.id.tx_jmlNotifPending_home);
        tx_notif_proses =(TextView)findViewById(R.id.tx_jmlNotifProses_home);
         tx_senin1 =(TextView)findViewById(R.id.senin1);
         tx_senin2 =(TextView)findViewById(R.id.senin2);
         tx_selasa1 =(TextView)findViewById(R.id.selasa1);
         tx_selasa2 =(TextView)findViewById(R.id.selasa2);
         tx_rabu1 =(TextView)findViewById(R.id.rabu1);
         tx_rabu2 =(TextView)findViewById(R.id.rabu2);
         tx_kamis1 =(TextView)findViewById(R.id.kamis1);
         tx_kamis2 =(TextView)findViewById(R.id.kamis2);
         tx_jumat1 =(TextView)findViewById(R.id.jumat1);
         tx_jumat2 =(TextView)findViewById(R.id.jumat2);
        photoViewAttacher = new PhotoViewAttacher(foto_profil);
        photoViewAttacher.setZoomable(false);

        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        String a = sharedPreferences.getString("mu_nama","");
        String b = sharedPreferences.getString("mp_nama","");
        mp_nama = sharedPreferences.getString("mp_nama","");

        if (sharedPreferences.getString("mu_flag","").equals("0")){
           notifikasi(mp_nama);
        }else{
            notifikasi("");
        }
        Picasso .get()
                .load(sharedPreferences.getString("mu_logo", Server.URL+"pengaturan/images_profil/image_profil_default.png"))
                .error(R.drawable.user_icon)
                .centerCrop()
                .fit()
                .transform(new com.asep.pelaporan_imaje.Picasso())
                .into(foto_profil);

        nama.setText(a);
        pt.setText(b.toUpperCase());
        getData();

//        code image slider
        ImageSlider imageSlider=findViewById(R.id.slider);

        FrameLayout frameLayout =(FrameLayout)findViewById(R.id.frame_image_home);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,WebView.class);
                startActivity(intent);
            }
        });


        List<SlideModel> slideModels=new ArrayList<>();
        slideModels.add(new SlideModel("http://192.168.43.103/pelaporan_imaje/images/slider/slide1.png"));
        slideModels.add(new SlideModel("http://192.168.43.103/pelaporan_imaje/images/slider/slide2.png"));
        slideModels.add(new SlideModel("http://192.168.43.103/pelaporan_imaje/images/slider/slide3.png"));
        slideModels.add(new SlideModel("http://192.168.43.103/pelaporan_imaje/images/slider/slide4.png"));
        imageSlider.setImageList(slideModels,true);

//        initial button
        Button bt_pending = (Button)findViewById(R.id.bt_pelPending_home);
        bt_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Pelaporan.class);
                intent.putExtra("statusLap","Pending");
                startActivity(intent);
            }
        });
        Button bt_proses = (Button)findViewById(R.id.bt_pelProses_home);
        bt_proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Pelaporan.class);
                intent.putExtra("statusLap","Proses");
                startActivity(intent);
            }
        });
        Button bt_selesai = (Button)findViewById(R.id.bt_pelaporanSelesai_home);
        bt_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Pelaporan.class);
                intent.putExtra("statusLap","Selesai");
                startActivity(intent);
            }
        });


        Button bt_lainnya = (Button)findViewById(R.id.bt_lainya);
        bt_lainnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,DetailHome.class);
                startActivity(intent);
            }
        });
        Button bt_datamesin = (Button)findViewById(R.id.bt_datamesin);
        bt_datamesin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,DataMesin.class);
                intent.putExtra("id_intent","data_mesin");
                startActivity(intent);
            }
        });
        Button bt_sparepert = (Button)findViewById(R.id.bt_sparepart_home);
        bt_sparepert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Sparepart.class);
                startActivity(intent);
            }
        });
        final Button bt_jadwalPm = (Button)findViewById(R.id.bt_jadwalPm_home);
        bt_jadwalPm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,JadwalPm.class);
                startActivity(intent);
            }
        });
        Button bt_pengaturan = (Button)findViewById(R.id.bt_pengaturan_home);
        bt_pengaturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Pengaturan.class);
                startActivity(intent);
            }
        });
        Button bt_buatlpaoran = (Button)findViewById(R.id.bt_buatlaporan_home);
        if (sharedPreferences.getString("mu_flag","").equals("0")) {
            bt_buatlpaoran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Home.this, BuatLaporan.class);
                    startActivity(intent);
                }
            });
        }else{
            bt_buatlpaoran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Menu ini hanya untuk pelanggan",Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button bt_kontak = (Button)findViewById(R.id.bt_kontak_home);
        bt_kontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Kontak.class);
                startActivity(intent);
            }
        });
        Button bt_laporan = (Button)findViewById(R.id.bt_laporan_home);
        bt_laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Laporan.class);
                startActivity(intent);
            }
        });
        LinearLayout jadwal =(LinearLayout)findViewById(R.id.ll_jadwal_home);
        jadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Home.this,JadwalKunjungan.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.option,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_share :
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"isi");
                intent.putExtra(Intent.EXTRA_SUBJECT,"subject");
                startActivity(Intent.createChooser(intent,"Share via"));
                return true;
            case R.id.action_about_apk :
                Intent i =new Intent(Home.this,AboutApk.class);
                finish();
                startActivity(i);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void notifikasi(String namaPt){
        StringRequest strReq = new StringRequest(Request.Method.GET, Server.URL+"menu_utama/get_jml_pelaporan.php?mp_nama="+namaPt.replace(" ","%20"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: " , response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    pending = jObj.getString("pending");
                    proses = jObj.getString("proses");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                if (!pending.equals("0")){
                    tx_notif_pending.setText(pending);
                    tx_notif_pending.setVisibility(View.VISIBLE);
                }
                if (!proses.equals("0")){
                    tx_notif_proses.setText(proses);
                    tx_notif_proses.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d( "Response Error: " , error.getMessage());
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void getData(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "jadwal_kunjungan/select_data.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                itemJadwalKunjungans = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Home.ItemJadwalKunjungan myitemJadwalKunjungan = new Home.ItemJadwalKunjungan(
                                jsonObject.getString("mj_hari"),
                                jsonObject.getString("mj_pelanggan_1"),
                                jsonObject.getString("mj_pelanggan_2")
                        );
                        itemJadwalKunjungans.add(myitemJadwalKunjungan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tx_senin1.setText(itemJadwalKunjungans.get(0).pel_1);
                tx_senin2.setText(itemJadwalKunjungans.get(0).pel_2);
                tx_selasa1.setText(itemJadwalKunjungans.get(1).pel_1);
                tx_selasa2.setText(itemJadwalKunjungans.get(1).pel_2);
                tx_rabu1.setText(itemJadwalKunjungans.get(2).pel_1);
                tx_rabu2.setText(itemJadwalKunjungans.get(2).pel_2);
                tx_kamis1.setText(itemJadwalKunjungans.get(3).pel_1);
                tx_kamis2.setText(itemJadwalKunjungans.get(3).pel_2);
                tx_jumat1.setText(itemJadwalKunjungans.get(4).pel_1);
                tx_jumat2.setText(itemJadwalKunjungans.get(4).pel_2);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    class ItemJadwalKunjungan{
        String hari,pel_1,pel_2;

        public ItemJadwalKunjungan(String hari, String pel_1, String pel_2) {
            this.hari = hari;
            this.pel_1 = pel_1;
            this.pel_2 = pel_2;
        }

        public String getHari() {
            return hari;
        }

        public void setHari(String hari) {
            this.hari = hari;
        }

        public String getPel_1() {
            return pel_1;
        }

        public void setPel_1(String pel_1) {
            this.pel_1 = pel_1;
        }

        public String getPel_2() {
            return pel_2;
        }

        public void setPel_2(String pel_2) {
            this.pel_2 = pel_2;
        }
    }

}
