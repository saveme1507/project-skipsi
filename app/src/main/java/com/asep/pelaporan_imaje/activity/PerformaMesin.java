package com.asep.pelaporan_imaje.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerformaMesin extends AppCompatActivity {
    RecyclerView recyclerView;
    Button button;
    RecyclerView.LayoutManager layoutManager;
    PerformaMesin.RecycleAdapterPerforma recycleAdapterPerforma;
    List<PerformaMesin.ItemPerforma> itemPerformas;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> barLabels;
    BarDataSet barDataSet;
    BarData barData;
    BarChart chart1;
    TextView tx_sn,tx_tipe,tx_line,tx_pm;
    String sn,tipe,line,pm,id;
    private Bitmap bmp,scalebmp;
    private int pageWidth = 1322;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performa_mesin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_performamesin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        chart1 = (BarChart) findViewById(R.id.chart_performamesin);
        tx_tipe =(TextView)findViewById(R.id.tx_mmTipe_perfom);
        tx_sn   =(TextView)findViewById(R.id.tx_mmSn_perfom);
        tx_line =(TextView)findViewById(R.id.tx_mmLine_perfom);
        tx_pm   =(TextView)findViewById(R.id.tx_mmLastPm_perfom);
        button = findViewById(R.id.cetak_item_performa);
        recyclerView =(RecyclerView)findViewById(R.id.recycle_item_performa);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        id  = getIntent().getStringExtra("mm_id");
        tipe = getIntent().getStringExtra("mm_tipe");
        sn  = getIntent().getStringExtra("mm_sn");
        line =getIntent().getStringExtra("mm_line");
        pm   =getIntent().getStringExtra("mm_lastPm");

        tx_tipe.setText(tipe);
        tx_sn.setText(sn);
        tx_pm.setText(DateFormat.dd_mmm_yyyy(pm));
        tx_line.setText(line);

        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.pdf_logo_printech);
        scalebmp = Bitmap.createScaledBitmap(bmp,247,110,false);

        dataGrafik();
        getDataHistoriPart(id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        if (ContextCompat.checkSelfPermission(PerformaMesin.this,
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
                                ActivityCompat.requestPermissions(PerformaMesin.this,
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

    public class MyValueFormatter extends ValueFormatter implements IValueFormatter{
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler){
            return "";
        }
    }
    private void dataGrafik(){
        StringRequest strReq = new StringRequest(Request.Method.GET, Server.URL+"data_mesin/select_performa.php?mm_id="+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: " ,response.toString());
                try {
                    barEntries = new ArrayList<BarEntry>();
                    barLabels = new ArrayList<String>();
                    JSONObject jObj = new JSONObject(response);
                        barLabels.add("");
                        barEntries.add(new BarEntry(1, Float.parseFloat(jObj.getString("jml3"))));
                        barLabels.add(DateFormat.dateLabelChart(jObj.getString("bulan3")));
                        barEntries.add(new BarEntry(2, Float.parseFloat(jObj.getString("jml2"))));
                        barLabels.add(DateFormat.dateLabelChart(jObj.getString("bulan2")));
                        barEntries.add(new BarEntry(3, Float.parseFloat(jObj.getString("jml1"))));
                        barLabels.add(DateFormat.dateLabelChart(jObj.getString("bulan1")));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                barDataSet = new BarDataSet(barEntries, "Jumlah kerusakan perbulan");
                barDataSet.setValueFormatter(new MyValueFormatter());
                barData = new BarData(barDataSet);
                chart1.getXAxis().setValueFormatter(
                        new IndexAxisValueFormatter(barLabels));
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                chart1.setData(barData);
                chart1.animateY(3000);
                chart1.getDescription().setEnabled(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d( "Login Error: " , error.getMessage());
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void getDataHistoriPart(String mm_id){
        String url= Server.URL + "data_mesin/pergantian_part.php?mm_id="+mm_id;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url.replace(" ","%20"),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response :",response.toString());
                itemPerformas = new ArrayList<>();
                if (response.length()==0){
                    button.setVisibility(View.GONE);
                }else{
                    button.setVisibility(View.VISIBLE);
                }
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        PerformaMesin.ItemPerforma myiItemPerforma = new PerformaMesin.ItemPerforma(
                                jsonObject.getString("hs_id"),
                                jsonObject.getString("hs_tgl"),
                                jsonObject.getString("hs_pn"),
                                jsonObject.getString("hs_nama"),
                                jsonObject.getString("mp_nama")
                        );
                        itemPerformas.add(myiItemPerforma);
                        recycleAdapterPerforma  = new PerformaMesin.RecycleAdapterPerforma(itemPerformas,PerformaMesin.this);
                        recycleAdapterPerforma.notifyDataSetChanged();
                        recyclerView.setAdapter(recycleAdapterPerforma);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Response :",error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
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
        String judul="DATA PERGANTIAN SPAREPART "+itemPerformas.get(0).mp_nama;
        canvas.drawText( judul.toUpperCase(),1322/2,220,paint);

        //header
        fieldDetail(paint,canvas,86,300,"Tipe");
        fieldDetail(paint,canvas,86,330,"SN");
        fieldDetail(paint,canvas,86,360,"Line");
        fieldDetail(paint,canvas,140,300,":");
        fieldDetail(paint,canvas,140,330,":");
        fieldDetail(paint,canvas,140,360,":");
        fieldDetail(paint,canvas,150,300,tipe);
        fieldDetail(paint,canvas,150,330,sn);
        fieldDetail(paint,canvas,150,360,line);

        //tabel header
        canvas.drawLine(86,370,pageWidth-86,370,paint); //horisontal atas
        canvas.drawLine(86,400,pageWidth-86,400,paint); //horisontal bawah
        canvas.drawLine(86,370,86,400,paint); //verical start
        canvas.drawLine(130,370,130,400,paint); //vertical no
        canvas.drawLine(350,370,350,400,paint); //vertical tgl
        canvas.drawLine(650,370,650,400,paint); //vertical pn
        canvas.drawLine(pageWidth-86,370,pageWidth-86,400,paint); //vertical sp
        //field header
        fieldHeader(paint,canvas,107,395,"NO");
        fieldHeader(paint,canvas,240,395,"TANGGAL");
        fieldHeader(paint,canvas,500,395,"PART NUMBER");
        fieldHeader(paint,canvas,943,395,"SPAREPART");

        //ISI
        for (i=0; i < itemPerformas.size(); i++){
            int col_awal =  (i)*30+400;
            int col_akhir = (i+1)*30+400;
            fieldTable(canvas,paint,col_awal,col_akhir,String.valueOf(i+1),
                    DateFormat.dd_mmm_yyyy(itemPerformas.get(i).hs_tgl),
                    itemPerformas.get(i).hs_pn,
                    itemPerformas.get(i).hs_nama);
        }

        pdfDocument.finishPage(page);
        reqPermisionStorege();
        String path ="/storage/emulated/0/Download/";
        File file = new File(path+"Data Pergantian part mesin -"+sn+".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplicationContext(),"Data pergantian sparepart berhasil disimpan "+path,Toast.LENGTH_LONG).show();
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
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20f);
        canvas.drawText(text,x,y,paint);
    }
    private void fieldTable(Canvas canvas,Paint paint,int hy_awal,int hy_akhir, String no,String type,String sn,String line){
        canvas.drawLine(86,hy_akhir,pageWidth-86,hy_akhir,paint); //horisontal bawah, start awal 300, interval 30
        canvas.drawLine(86,hy_awal,86,hy_akhir,paint); //verical start
        canvas.drawLine(130,hy_awal,130,hy_akhir,paint); //vertical no
        canvas.drawLine(350,hy_awal,350,hy_akhir,paint); //vertical tgl
        canvas.drawLine(650,hy_awal,650,hy_akhir,paint); //vertical pn
        canvas.drawLine(pageWidth-86,hy_awal,pageWidth-86,hy_akhir,paint); //vertical sp
        //field header
        fieldDetail(paint,canvas,100,hy_akhir-5,no);
        fieldDetail(paint,canvas,140,hy_akhir-5,type);
        fieldDetail(paint,canvas,360,hy_akhir-5,sn);
        fieldDetail(paint,canvas,660,hy_akhir-5,line);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ItemPerforma{
        String hs_id,hs_tgl,hs_pn,hs_nama,mp_nama;

        public ItemPerforma(String hs_id, String hs_tgl, String hs_pn, String hs_nama, String mp_nama) {
            this.hs_id = hs_id;
            this.hs_tgl = hs_tgl;
            this.hs_pn = hs_pn;
            this.hs_nama = hs_nama;
            this.mp_nama = mp_nama;
        }

        public String getHs_id() {
            return hs_id;
        }

        public void setHs_id(String hs_id) {
            this.hs_id = hs_id;
        }

        public String getHs_tgl() {
            return hs_tgl;
        }

        public void setHs_tgl(String hs_tgl) {
            this.hs_tgl = hs_tgl;
        }

        public String getHs_pn() {
            return hs_pn;
        }

        public void setHs_pn(String hs_pn) {
            this.hs_pn = hs_pn;
        }

        public String getHs_nama() {
            return hs_nama;
        }

        public void setHs_nama(String hs_nama) {
            this.hs_nama = hs_nama;
        }

        public String getMp_nama() {
            return mp_nama;
        }

        public void setMp_nama(String mp_nama) {
            this.mp_nama = mp_nama;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    class RecycleAdapterPerforma extends RecyclerView.Adapter<PerformaMesin.RecycleAdapterPerforma.MyViewHolderPerforma>{
        List<PerformaMesin.ItemPerforma> itemPerformas;
        Context context;

        public RecycleAdapterPerforma(List<ItemPerforma> itemPerformas, Context context) {
            this.itemPerformas = itemPerformas;
            this.context = context;
        }

        @Override
        public PerformaMesin.RecycleAdapterPerforma.MyViewHolderPerforma onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_perfoma,viewGroup,false);
            return  new PerformaMesin.RecycleAdapterPerforma.MyViewHolderPerforma(view);
        }

        @Override
        public void onBindViewHolder(PerformaMesin.RecycleAdapterPerforma.MyViewHolderPerforma myViewHolderPerforma, int i) {
            myViewHolderPerforma.tx_nama.setText(itemPerformas.get(i).hs_nama);
            myViewHolderPerforma.tx_tgl.setText(DateFormat.dd_mmm_yyyy(itemPerformas.get(i).hs_tgl));
        }

        @Override
        public int getItemCount() {
            return itemPerformas.size();
        }

        public class MyViewHolderPerforma extends RecyclerView.ViewHolder{
            TextView tx_tgl,tx_nama;
            public MyViewHolderPerforma(View itemView) {
                super(itemView);
                tx_tgl =itemView.findViewById(R.id.tx_tgl_itemperforma);
                tx_nama =itemView.findViewById(R.id.tx_namapart_itemperforma);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PerformaMesin.this,DetailLaporan.class);
                        intent.putExtra("id_intent","per_part");
                        intent.putExtra("hlm_id",itemPerformas.get(getAdapterPosition()).hs_id);
                        intent.putExtra("flag","acc");
                        startActivity(intent);
                    }
                });
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

}


