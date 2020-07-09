package com.asep.pelaporan_imaje.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DetailLaporan extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextView tx_hlmID,tx_header,tx_mesin,tx_parameter,tx_ket,tx_paraNama;
    String hlm_id,header,mesin,parameter,keterangan,flag,teknisi,string_ttd,namaLap;
    SignaturePad signaturePad;
    Button bt_simpan,bt_ulangi,bt_cetak;
    Bitmap bmp_ttd,bmp,scalebmp,bmp_signatur;
    ImageView iv_ttd;
    int pageWidth = 1322;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laporan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_detailLaporan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);

        tx_hlmID    =(TextView)findViewById(R.id.tx_hlm_id_detailLap);
        tx_header   =(TextView)findViewById(R.id.tx_header_detailLap);
        tx_mesin    =(TextView)findViewById(R.id.tx_mesin_detailLap);
        tx_parameter=(TextView)findViewById(R.id.tx_parameter_detailLap);
        tx_ket      =(TextView)findViewById(R.id.tx_keterangan_detailLap);
        tx_paraNama =findViewById(R.id.tx_paraNama_datailLap);
        bt_simpan   =(Button)findViewById(R.id.bt_simpan_detailLap);
        bt_ulangi   =(Button)findViewById(R.id.bt_ulangi_detailLap);
        bt_cetak    =findViewById(R.id.bt_cetak_detailLap);
        signaturePad=(SignaturePad)findViewById(R.id.signaturePad);
        iv_ttd      =findViewById(R.id.iv_ttd_detailLap);
        RelativeLayout rl_signature =findViewById(R.id.rl_acc);

        iv_ttd.setVisibility(View.VISIBLE);

        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.pdf_logo_printech);
        scalebmp = Bitmap.createScaledBitmap(bmp,247,110,false);

        String hlmId = getIntent().getStringExtra("hlm_id"); //id laporan
        final String id_intent = getIntent().getStringExtra("id_intent"); //membedakan laporan &pergantian sparepart
        flag = getIntent().getStringExtra("flag"); //untuk hiden button

        if (!sharedPreferences.getString("mu_flag","").equals("0")){
            rl_signature.setVisibility(View.GONE);
        }

        if (id_intent.equals("lap_mesin")){
            getDataLapMesin(hlmId);
            namaLap="Laporan Mesin";
        }else if (id_intent.equals("per_part")){
            getDataPerPart(hlmId);
            namaLap="Laporan Pergantian Sparepart";
            tx_paraNama.setText("SparePart: ");
        }

        if (flag.equals("acc")){
            rl_signature.setVisibility(View.GONE);
            bt_cetak.setVisibility(View.VISIBLE);
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
                bmp_signatur = signaturePad.getSignatureBitmap();
                if (id_intent.equals("lap_mesin")){
                    simpanTtd_mesin(bmp_signatur);
                }else{
                    simpanTtd_part(bmp_signatur);
                }

            }
        });
        bt_ulangi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear();
            }
        });
        bt_cetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bmp_ttd = BitmapFormat.getBitmapFromImageView(iv_ttd);
                createPDF();
            }
        });
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
        if (ContextCompat.checkSelfPermission(DetailLaporan.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(DetailLaporan.this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(DetailLaporan.this,
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
                        teknisi = jObj.getString("teknisi");
                        string_ttd = jObj.getString("ttd");
                    }
                    tx_hlmID.setText("No: "+hlm_id);
                    tx_header.setText(header.replace("*","\n\n"));
                    tx_mesin.setText(mesin.replace("*","\n\n"));
                    tx_parameter.setText(parameter.replace("*","\n\n"));
                    tx_ket.setText(keterangan);
                    if (!string_ttd.equals("")){
                        Picasso.get().load(string_ttd).into(iv_ttd);
                    }
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
    private void getDataPerPart(String hlmId){
        StringRequest stringRequest = new StringRequest( Server.URL+"sparepart/select_lapPart.php?hlm_id="+hlmId,new Response.Listener<String>() {
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
                        string_ttd = jObj.getString("ttd");
                        teknisi="";
                    }
                    tx_hlmID.setText("No: "+hlm_id);
                    tx_header.setText(header.replace("*","\n\n"));
                    tx_mesin.setText(mesin.replace("*","\n\n"));
                    tx_parameter.setText(parameter.replace("*","\n\n"));
                    tx_ket.setText(keterangan);
                    if (!string_ttd.equals("")){
                        Picasso.get().load(string_ttd).into(iv_ttd);
                    }
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

    private void simpanTtd_mesin(final Bitmap bitmap) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Server.URL+"buat_lapPengerjaan/simpanTtd_mesin.php",new Response.Listener<String>() {
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
                params.put("hlm_ttd", BitmapFormat.getStringImage(bitmap));
                params.put("hlm_id", hlm_id);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void simpanTtd_part(final Bitmap bitmap) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Server.URL+"sparepart/simpan_ttdPart.php",new Response.Listener<String>() {
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
                params.put("hlm_ttd", BitmapFormat.getStringImage(bitmap));
                params.put("hlm_id", hlm_id);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void createPDF(){

        PdfDocument pdfDocument = new PdfDocument();
        Paint paintLogo = new Paint();
        Paint paintTitle = new Paint();
        TextPaint textPaint = new TextPaint();
        StaticLayout staticLayout;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth,1870,1).create();//height normal A4=1870
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        //Logo
        canvas.drawBitmap(scalebmp,86,34,paintLogo);
        //Nomer Laporan
        paintLogo.setColor(Color.BLACK);
        paintLogo.setTextSize(25f);
        paintLogo.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("NO: "+hlm_id,1200,95,paintLogo);

        //title
        paintTitle.setTextAlign(Paint.Align.CENTER);
        paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintTitle.setTextSize(40f);
        canvas.drawText("MACHINE REPORT",1322/2,200,paintTitle);

        //header
        textPaint.setTextSize(22);
        staticLayout = new StaticLayout(header.replace("*","\n")
                ,textPaint,450, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, false);
        canvas.save();
        canvas.translate(86, 245);
        staticLayout.draw(canvas);
        canvas.restore();

        //teknisi
        if (!teknisi.equals("")){
            paintTitle.setTextSize(22);
            paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText(teknisi,pageWidth-300,260,paintTitle);
        }

        paintTitle.setTextAlign(Paint.Align.LEFT);
        paintTitle.setTextSize(22f);
        canvas.drawLine(86,360,pageWidth-86,360,paintTitle);

        //mesin
        paintTitle.setTextSize(22);
        paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Mesin:",86,410,paintTitle);

        textPaint.setTextSize(22);
        staticLayout = new StaticLayout(mesin.replace("*","\n"),textPaint,450, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, false);
        canvas.save();
        canvas.translate(86, 430);
        staticLayout.draw(canvas);
        canvas.restore();

        //parameter
        String paraNama;
        if (teknisi.equals("")){
            paraNama="SparaPart:";
        }else {
            paraNama="Parameter:";
        }
        paintTitle.setTextSize(22);
        paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(paraNama,495,410,paintTitle);

        textPaint.setTextSize(22);
        staticLayout = new StaticLayout(parameter.replace("*","\n"),textPaint,200, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, false);
        canvas.save();
        canvas.translate(497, 430);
        staticLayout.draw(canvas);
        canvas.restore();

        //keterangan
        paintTitle.setTextSize(22);
        paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Keterangan:",800,410,paintTitle);

        textPaint.setTextSize(22);
        staticLayout = new StaticLayout(keterangan,textPaint,450, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, false);
        canvas.save();
        canvas.translate(800, 430);
        staticLayout.draw(canvas);
        canvas.restore();

        paintTitle.setTextSize(22);
        paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Paraf Pelanggan:",pageWidth-300,880,paintTitle);

        Bitmap scalettd = Bitmap.createScaledBitmap(bmp_ttd,180,180,false);
        canvas.drawBitmap(scalettd,pageWidth-300,900,paintLogo);

        pdfDocument.finishPage(page);
        reqPermisionStorege();
        String path ="/storage/emulated/0/Download/";
        File file = new File(path+namaLap+"-"+hlm_id+".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplicationContext(),"Laporan disimpan "+path,Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();

    }



}
