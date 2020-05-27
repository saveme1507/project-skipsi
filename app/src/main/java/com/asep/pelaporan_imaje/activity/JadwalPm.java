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
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class JadwalPm extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    RecyclerView recyclerViewJadwalpm;
    RecyclerView.LayoutManager layoutManagerJadwalpm;
    RecycleAdapterJadwalpm recycleAdapterJadwalpm;
    List<ItemJadwalpm> itemJadwalpms;
    NiceSpinner niceSpinner;
    private ArrayList<String> data = new ArrayList<>();
    private TextView tx_judul;
    private FloatingActionButton float_cetak;
    private Bitmap bmp,scalebmp;
    private int pageWidth = 1322;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_pm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_jadwalpm);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        float_cetak= findViewById(R.id.floating_cetak_pm);
        recyclerViewJadwalpm = (RecyclerView)findViewById(R.id.recycle_item_jadwalpm);
        niceSpinner = (NiceSpinner)findViewById(R.id.spinner_sort_Jadwalpm);
        tx_judul    = (TextView)findViewById(R.id.tx_judul_jadwalpm);

        layoutManagerJadwalpm = new LinearLayoutManager(this);
        recyclerViewJadwalpm.setLayoutManager(layoutManagerJadwalpm);
        recyclerViewJadwalpm.setHasFixedSize(true);
        itemJadwalpms = new ArrayList<>();

        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.pdf_logo_printech);
        scalebmp = Bitmap.createScaledBitmap(bmp,247,110,false);

        if (sharedPreferences.getString("mu_flag","").equals("0")){
            tx_judul.setText(sharedPreferences.getString("mp_nama",""));
            niceSpinner.setVisibility(View.GONE);
        }else{
            tx_judul.setText("All");
            getDataMesin("");
        }

        if (niceSpinner.getText().equals("All")){
            float_cetak.hide();
        }else{
            float_cetak.show();
            float_cetak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createPDF();
                }
            });
        }

        dataSpinner();
        getDataMesin(tx_judul.getText().toString());

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tx_judul.setText(data.get(i));
                if ("All".equalsIgnoreCase(tx_judul.getText().toString())) {
                    getDataMesin("");
                } else {
                    getDataMesin(tx_judul.getText().toString().replace("PT.","").toLowerCase());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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
        if (ContextCompat.checkSelfPermission(JadwalPm.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(DetailLaporan.this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(JadwalPm.this,
                                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }

    private void getDataMesin(String namaPT){
        String url= Server.URL + "data_mesin/select_datamesin_by_namaPT.php?nama_pt="+namaPT;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url.replace(" ","%20"),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response: ",response.toString());
                itemJadwalpms.clear();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        ItemJadwalpm myitemJadwalpm = new ItemJadwalpm(
                                jsonObject.getString("mm_id"),
                                jsonObject.getString("mm_sn"),
                                jsonObject.getString("mm_tipe"),
                                jsonObject.getString("mm_last_pm"),
                                jsonObject.getString("mm_posisi")
                        );
                        itemJadwalpms.add(myitemJadwalpm);
                        recycleAdapterJadwalpm  = new RecycleAdapterJadwalpm(itemJadwalpms,JadwalPm.this);
                        recycleAdapterJadwalpm.notifyDataSetChanged();
                        recyclerViewJadwalpm.setAdapter(recycleAdapterJadwalpm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    private void dataSpinner(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "data_mesin/select_namaPT.php",new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("response :",response.toString());
                data.add("All");
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        data.add(jsonObject.getString("mp_nama"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                niceSpinner.attachDataSource(data);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
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
        String judul="Data Jadwal PM Mesin "+tx_judul.getText().toString();
        canvas.drawText( judul.toUpperCase(),1322/2,220,paint);

        //tabel header
        canvas.drawLine(86,270,pageWidth-86,270,paint); //horisontal atas
        canvas.drawLine(86,300,pageWidth-86,300,paint); //horisontal bawah
        canvas.drawLine(86,270,86,300,paint); //verical start
        canvas.drawLine(130,270,130,300,paint); //vertical no
        canvas.drawLine(350,270,350,300,paint); //vertical tipe mesin
        canvas.drawLine(580,270,580,300,paint); //vertical sn mesin
        canvas.drawLine(800,270,800,300,paint); //vertical line
        canvas.drawLine(1000,270,1000,300,paint); //veertical last pm
        canvas.drawLine(pageWidth-86,270,pageWidth-86,300,paint); //vertical next pm
        //field header
        fieldHeader(paint,canvas,107,295,"NO");
        fieldHeader(paint,canvas,240,295,"TIPE MESIN");
        fieldHeader(paint,canvas,465,295,"SERIAL NUMBER");
        fieldHeader(paint,canvas,690,295,"LINE");
        fieldHeader(paint,canvas,900,295,"LAST PM");
        fieldHeader(paint,canvas,1118,295,"NEXT PM");

        //ISI
        for (i=0; i < itemJadwalpms.size(); i++){
            int col_awal =  (i)*30+300;
            int col_akhir = (i+1)*30+300;
            fieldTable(canvas,paint,col_awal,col_akhir,String.valueOf(i+1),
                    itemJadwalpms.get(i).mm_tipe,
                    itemJadwalpms.get(i).mm_sn,
                    itemJadwalpms.get(i).mm_posisi,
                    DateFormat.dd_mmm_yyyy(itemJadwalpms.get(i).mm_last_pm),
                    DateFormat.dateNextPm(itemJadwalpms.get(i).mm_last_pm));
        }

        pdfDocument.finishPage(page);
        reqPermisionStorege();
        String path ="/storage/emulated/0/Download/";
        File file = new File(path+"DataPM-"+tx_judul.getText().toString()+".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplicationContext(),"Data PM berhasil disimpan "+path,Toast.LENGTH_LONG).show();
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
    private void fieldTable(Canvas canvas,Paint paint,int hy_awal,int hy_akhir, String no,String type,String sn,String line,String last,String next){
        canvas.drawLine(86,hy_akhir,pageWidth-86,hy_akhir,paint); //horisontal bawah, start awal 300, interval 30
        canvas.drawLine(86,hy_awal,86,hy_akhir,paint); //verical start
        canvas.drawLine(130,hy_awal,130,hy_akhir,paint); //vertical no
        canvas.drawLine(350,hy_awal,350,hy_akhir,paint); //vertical tipe mesin
        canvas.drawLine(580,hy_awal,580,hy_akhir,paint); //vertical sn mesin
        canvas.drawLine(800,hy_awal,800,hy_akhir,paint); //vertical line
        canvas.drawLine(1000,hy_awal,1000,hy_akhir,paint); //veertical last pm
        canvas.drawLine(pageWidth-86,hy_awal,pageWidth-86,hy_akhir,paint); //vertical next pm
        //field header
        fieldDetail(paint,canvas,107,hy_akhir-5,no);
        fieldDetail(paint,canvas,240,hy_akhir-5,type);
        fieldDetail(paint,canvas,465,hy_akhir-5,sn);
        fieldDetail(paint,canvas,690,hy_akhir-5,line);
        fieldDetail(paint,canvas,900,hy_akhir-5,last);
        fieldDetail(paint,canvas,1118,hy_akhir-5,next);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private class ItemJadwalpm{
        String mm_id,mm_sn,mm_tipe,mm_last_pm,mm_posisi;

        public ItemJadwalpm(String mm_id, String mm_sn, String mm_tipe, String mm_last_pm,String mm_posisi) {
            this.mm_id = mm_id;
            this.mm_sn = mm_sn;
            this.mm_tipe = mm_tipe;
            this.mm_last_pm = mm_last_pm;
            this.mm_posisi = mm_posisi;
        }

        public String getMm_id() {
            return mm_id;
        }

        public void setMm_id(String mm_id) {
            this.mm_id = mm_id;
        }

        public String getMm_sn() {
            return mm_sn;
        }

        public void setMm_sn(String mm_sn) {
            this.mm_sn = mm_sn;
        }

        public String getMm_tipe() {
            return mm_tipe;
        }

        public void setMm_tipe(String mm_tipe) {
            this.mm_tipe = mm_tipe;
        }

        public String getMm_last_pm() {
            return mm_last_pm;
        }

        public void setMm_last_pm(String mm_last_pm) {
            this.mm_last_pm = mm_last_pm;
        }

        public String getMm_posisi() {
            return mm_posisi;
        }

        public void setMm_posisi(String mm_posisi) {
            this.mm_posisi = mm_posisi;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private class RecycleAdapterJadwalpm extends RecyclerView.Adapter<RecycleAdapterJadwalpm.MyViewHolderJadwalpm>{
        List<ItemJadwalpm> itemJadwalpms;
        Context context;

        public RecycleAdapterJadwalpm(List<ItemJadwalpm> itemJadwalpms, Context context) {
            this.itemJadwalpms = itemJadwalpms;
            this.context = context;
        }

        @Override
        public MyViewHolderJadwalpm onCreateViewHolder( ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_jadwal_pm,viewGroup,false);
            return new MyViewHolderJadwalpm(view);
        }

        @Override
        public void onBindViewHolder( MyViewHolderJadwalpm myViewHolderJadwalpm, int i) {
            myViewHolderJadwalpm.mm_id.setText(itemJadwalpms.get(i).mm_id);
            myViewHolderJadwalpm.mm_last_pm.setText(DateFormat.dateNextPm(itemJadwalpms.get(i).mm_last_pm));
            myViewHolderJadwalpm.mm_tipe.setText(itemJadwalpms.get(i).mm_tipe.toUpperCase());
            myViewHolderJadwalpm.mm_sn.setText(itemJadwalpms.get(i).mm_sn);
        }

        @Override
        public int getItemCount() {
            return itemJadwalpms.size();
        }

        public class MyViewHolderJadwalpm extends RecyclerView.ViewHolder{
            TextView mm_id,mm_sn,mm_tipe,mm_last_pm;
            public MyViewHolderJadwalpm(@NonNull View itemView) {
                super(itemView);
                mm_id       =(TextView)itemView.findViewById(R.id.tx_id_jadwalpm);
                mm_last_pm  =(TextView)itemView.findViewById(R.id.tx_lastpm_jadwalpm);
                mm_tipe     =(TextView)itemView.findViewById(R.id.tx_tipe_jadwalpm);
                mm_sn       =(TextView)itemView.findViewById(R.id.tx_sn_jadwalpm);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(),HistoriPm.class);
                        intent.putExtra("mm_id",itemJadwalpms.get(getAdapterPosition()).mm_id);
                        intent.putExtra("tipe-sn",itemJadwalpms.get(getAdapterPosition()).mm_tipe.toUpperCase()+" "+itemJadwalpms.get(getAdapterPosition()).mm_sn).toString().replace(" ","");
                        intent.putExtra("last_pm",DateFormat.dd_mmm_yyyy(itemJadwalpms.get(getAdapterPosition()).mm_last_pm));
                        intent.putExtra("next_pm",DateFormat.dateNextPm(itemJadwalpms.get(getAdapterPosition()).mm_last_pm));
                        view.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////
}
