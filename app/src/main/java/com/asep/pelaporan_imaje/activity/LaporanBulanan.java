package com.asep.pelaporan_imaje.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.config.DateFormat;
import com.asep.pelaporan_imaje.server.Server;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LaporanBulanan extends AppCompatActivity {
    NiceSpinner nsNama, nsBulan;
    SharedPreferences sharedPreferences;
    final List<String> dataNama = new ArrayList<>();
    final List<String> dataId = new ArrayList<>();
    ArrayList<Report> reports = new ArrayList<>();
    List<String> listBulan = new LinkedList<>(Arrays.asList("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"));
    private Bitmap bmp,scalebmp;
    private int pageWidth = 1322;
    private int STORAGE_PERMISSION_CODE = 1;
    int indexPT,indexBulan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_bulanan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_LapBulanan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        nsNama = findViewById(R.id.nsPel_LapBul);
        nsBulan = findViewById(R.id.nsBulan_LapBul);
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);

        dataSpNama();
        nsBulan.attachDataSource(listBulan);
        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.pdf_logo_printech);
        scalebmp = Bitmap.createScaledBitmap(bmp,247,110,false);

        findViewById(R.id.btCetak_LapBul).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indexPT = nsNama.getSelectedIndex();
                indexBulan = nsBulan.getSelectedIndex();
                getData(dataId.get(indexPT), String.valueOf(indexBulan + 1));
            }
        });
    }

    private void dataSpNama() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "data_mesin/select_namaPT.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("response :", response.toString());
                dataId.clear();
                dataNama.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        dataId.add(jsonObject.getString("mp_id"));
                        dataNama.add(jsonObject.getString("mp_nama"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                nsNama.attachDataSource(dataNama);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void getData(String id, String bulan) {
        reports.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "report/bulanan.php?id=" + id + "&bulan=" + bulan, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("response :", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Report report = new Report(
                                jsonObject.getString("tgl"),
                                jsonObject.getString("pengerjaan"),
                                jsonObject.getString("tipe"),
                                jsonObject.getString("sn"),
                                jsonObject.getString("nama")
                        );
                        reports.add(report);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Data tidak ditemukan", Toast.LENGTH_LONG).show();
                }
                if (reports.size() != 0) {
                    dialogPrint();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public int compareTo(Request<JSONArray> other) {
                Log.e("empty", other.toString());
                return super.compareTo(other);
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void dialogPrint() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Print");
        builder.setMessage("Data ditemukan, print laporan?");
        builder.setCancelable(false);
        builder.setPositiveButton("Print", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createPDF();
//                Log.e("date: ",DateFormat.currentDataReport());
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    private void createPDF(){
        int i;
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth,1870,1).create();//height normal A4=1870
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        //Logo
        canvas.drawBitmap(scalebmp,86,50,paint);

        //title
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(30f);
        String judul="DATA PERBAIKAN DI "+dataNama.get(indexPT).toUpperCase()+" BULAN "+listBulan.get(indexBulan).toUpperCase();
        canvas.drawText( judul.toUpperCase(),1322/2,220,paint);

        //tabel header
        canvas.drawLine(86,270,pageWidth-86,270,paint); //horisontal atas
        canvas.drawLine(86,300,pageWidth-86,300,paint); //horisontal bawah
        canvas.drawLine(86,270,86,300,paint); //verical start
        canvas.drawLine(257,270,257,300,paint); //vertical tgl
        canvas.drawLine(541,270,541,300,paint); //vertical pegerjaan
        canvas.drawLine(687,270,687,300,paint); //vertical tipe
        canvas.drawLine(908,270,908,300,paint); //vertical sn
        canvas.drawLine(pageWidth-86,270,pageWidth-86,300,paint); //vertical teknisi
        //field header
        fieldHeader(paint,canvas,170,295,"TANGGAL");
        fieldHeader(paint,canvas,390,295,"PENGERJAAN");
        fieldHeader(paint,canvas,617,295,"TIPE");
        fieldHeader(paint,canvas,790,295,"SERIAL MESIN");
        fieldHeader(paint,canvas,1070,295,"TEKNISI");

        //ISI
        int col_akhir = 0;
        for (i=0; i < reports.size(); i++){
            int col_awal =  (i)*30+300;
            col_akhir = (i+1)*30+300;
            fieldTable(canvas,paint,col_awal,col_akhir,
                    reports.get(i).tgl,
                    reports.get(i).pengerjaan,
                    reports.get(i).tipe,
                    reports.get(i).sn,
                    reports.get(i).nama
                    );
        }
        fieldDetail(paint,canvas,1085,col_akhir+100, "Karawang, "+DateFormat.currentDataReport());
        fieldDetail(paint,canvas,1085,col_akhir+125, "Teknisi");
        fieldDetail(paint,canvas,1085,col_akhir+250, sharedPreferences.getString("mu_nama",null));

        pdfDocument.finishPage(page);
        reqPermisionStorege();
        String path ="/storage/emulated/0/Download/";
        File file = new File(path+"DATA AKTIVITAS DI "+dataNama.get(indexPT).toUpperCase()+" BULAN "+listBulan.get(indexBulan).toUpperCase()+".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplicationContext(),"Data laporan bulanan berhasil disimpan "+path,Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
    }

    private void fieldHeader(Paint paint, Canvas canvas, int x,int y,String text){
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(20f);
        canvas.drawText(text,x,y,paint);
    }
    private void fieldDetail(Paint paint, Canvas canvas, int x,int y,String text){
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20f);
        canvas.drawText(text,x,y,paint);
    }
    private void fieldTable(Canvas canvas,Paint paint,int hy_awal,int hy_akhir, String tgl,String pengerjaan,String tipe,String sn,String teknisi){
        canvas.drawLine(86,hy_akhir,pageWidth-86,hy_akhir,paint); //horisontal bawah, start awal 300, interval 30
        canvas.drawLine(86,hy_awal,86,hy_akhir,paint); //verical start
        canvas.drawLine(257,hy_awal,257,hy_akhir,paint); //vertical tgl
        canvas.drawLine(541,hy_awal,541,hy_akhir,paint); //vertical pengerjaan
        canvas.drawLine(687,hy_awal,687,hy_akhir,paint); //vertical tipe
        canvas.drawLine(908,hy_awal,908,hy_akhir,paint); //vertical sn
        canvas.drawLine(pageWidth-86,hy_awal,pageWidth-86,hy_akhir,paint); //vertical teknisi
        //field detail
        fieldDetail(paint,canvas,170,hy_akhir-5,tgl);
        fieldDetail(paint,canvas,390,hy_akhir-5,pengerjaan);
        fieldDetail(paint,canvas,617,hy_akhir-5,tipe);
        fieldDetail(paint,canvas,790,hy_akhir-5,sn);
        fieldDetail(paint,canvas,1070,hy_akhir-5,teknisi);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void reqPermisionStorege(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(DetailLaporan.this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LaporanBulanan.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(LaporanBulanan.this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(LaporanBulanan.this,
                                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    class Report {
        String tgl, pengerjaan, tipe, sn, nama;

        public Report(String tgl, String pengerjaan, String tipe, String sn, String nama) {
            this.tgl = tgl;
            this.pengerjaan = pengerjaan;
            this.tipe = tipe;
            this.sn = sn;
            this.nama = nama;
        }

        public String getTgl() {
            return tgl;
        }

        public void setTgl(String tgl) {
            this.tgl = tgl;
        }

        public String getPengerjaan() {
            return pengerjaan;
        }

        public void setPengerjaan(String pengerjaan) {
            this.pengerjaan = pengerjaan;
        }

        public String getTipe() {
            return tipe;
        }

        public void setTipe(String tipe) {
            this.tipe = tipe;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }
    }

}
