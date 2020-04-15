package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.asep.pelaporan_imaje.config.DateFormat;
import com.asep.pelaporan_imaje.server.Server;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMesin extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecycleAdapter recycleAdapter;
    List<Item> items;
    ArrayList<String> array_id_pt = new ArrayList<>();
    ArrayList<String> nama_pt = new ArrayList<>();
    ArrayList<String> data = new ArrayList<String>();
    NiceSpinner niceSpinner;
    TextView tx_judul;
    FloatingActionButton floatingActionButton;
    Spinner sp_palanggan;
    String id_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_mesin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_datamesin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recyclerView    = (RecyclerView)findViewById(R.id.recycle_item_data_mesin);
        niceSpinner     = (NiceSpinner)findViewById(R.id.spinner_sort_datamesin);
        tx_judul        = (TextView)findViewById(R.id.tx_judul);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floating_tambah_datamesin);
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        id_intent = getIntent().getStringExtra("id_intent");

        if (id_intent.equals("data_mesin")){
            floatingActionButton.hide();
            if (sharedPreferences.getString("mu_flag","").equals("0")){
                tx_judul.setText(sharedPreferences.getString("mp_nama",""));
                niceSpinner.setVisibility(View.GONE);
            }else{
                tx_judul.setText("");
            }
        }else if (id_intent.equals("pengaturan_data_mesin")){
            tx_judul.setText("");
        }

        layoutManager   = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        items = new ArrayList<>();

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
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditDataMesin("Tambah Data Mesin","","","","","","");
            }
        });
    }

    private void getDataMesin(String namaPT){
        String url=Server.URL + "data_mesin/select_datamesin_by_namaPT.php?nama_pt="+namaPT;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url.replace(" ","%20"),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                items.clear();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Item myitem = new Item(
                                jsonObject.getString("mm_id"),
                                jsonObject.getString("mm_tipe"),
                                jsonObject.getString("mm_sn"),
                                jsonObject.getString("mm_last_pm"),
                                jsonObject.getString("mm_posisi"),
                                jsonObject.getString("mp_id")
                        );
                        items.add(myitem);
                        recycleAdapter  = new RecycleAdapter(items,DataMesin.this);
                        recycleAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(recycleAdapter);
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
                        array_id_pt.add(jsonObject.getString("mp_id"));
                        nama_pt.add(jsonObject.getString("mp_nama"));
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
    private void insertDataMesin(final String sn, final String tipe, final String posisi, final String last_pm, final String id_pt){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"data_mesin/insert_datamesin.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        getDataMesin(sp_palanggan.getSelectedItem().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Tambah data mesin gagal",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Error :", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_sn", sn);
                params.put("mm_tipe", tipe);
                params.put("mm_posisi",posisi);
                params.put("mm_last_pm",last_pm);
                params.put("mm_id_pt",id_pt);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void updateDataMesin(final String id, final String sn, final String tipe, final String posisi, final String last_pm, final String id_pt){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"data_mesin/update_datamesin.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response :", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        getDataMesin(sp_palanggan.getSelectedItem().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Edit data mesin gagal",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response Error :", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_id",id);
                params.put("mm_sn", sn);
                params.put("mm_tipe", tipe);
                params.put("mm_posisi",posisi);
                params.put("mm_last_pm",last_pm);
                params.put("mm_id_pt",id_pt);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void deleteDataMesin(final String id){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"data_mesin/delete_datamesin.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        getDataMesin(tx_judul.getText().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Hapus data mesin gagal",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Error :", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_id",id);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    private void dialogEditDataMesin(String judul, final String id, String sn, String tipe, String posisi, String lastpm, final String id_pt){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DataMesin.this);
        View view=LayoutInflater.from(DataMesin.this).inflate(R.layout.dialog_edit_datamesin,null);
        alertBuilder.setView(view);
        alertBuilder.setCancelable(false);

        TextView tx_judul         =(TextView)view.findViewById(R.id.tx_judul_dialog_datamesin);
        final TextView tx_id      =(TextView)view.findViewById(R.id.tx_id_dialog_datamesin);
        final EditText et_sn      =(EditText)view.findViewById(R.id.et_sn_dialog_datamesin);
        final EditText et_tipe    =(EditText)view.findViewById(R.id.et_tipe_dialog_datamesin);
        final EditText et_posisi  =(EditText)view.findViewById(R.id.et_posisi_dialog_datamesin);
        final EditText et_lastPm  =(EditText)view.findViewById(R.id.et_lastPm_dialog_datamesin);
        sp_palanggan =(Spinner) view.findViewById(R.id.sp_namapt_dialog_datamesin);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DataMesin.this, android.R.layout.simple_spinner_item,nama_pt);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_palanggan.setAdapter(arrayAdapter);

        if (!id.equals("")) {
            tx_judul.setText(judul);
            tx_id.setText(id);
            et_sn.setText(sn);
            et_tipe.setText(tipe);
            et_posisi.setText(posisi);
            et_lastPm.setText(lastpm);
            sp_palanggan.setEnabled(false);
            sp_palanggan.setSelection(array_id_pt.indexOf(id_pt));
        }
        alertBuilder.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!id.equals("")) {
                    //update
                    updateDataMesin(
                            tx_id.getText().toString(),
                            et_sn.getText().toString(),
                            et_tipe.getText().toString(),
                            et_posisi.getText().toString(),
                            et_lastPm.getText().toString(),
                            id_pt
                    );
                }else{
                    //insert
                    insertDataMesin(
                            et_sn.getText().toString(),
                            et_tipe.getText().toString(),
                            et_posisi.getText().toString(),
                            et_lastPm.getText().toString(),
                            array_id_pt.get(nama_pt.indexOf(sp_palanggan.getSelectedItem().toString()))
                    );
                }
            }
        });
        alertBuilder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertBuilder.show();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder>{
        List<Item> items;
        Context context;

        public RecycleAdapter(List<Item> items, Context context){
            this.items = items;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_mesin,viewGroup,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
            String logo = items.get(i).getTipe();
            myViewHolder.mm_id.setText(items.get(i).getMm_id());
            myViewHolder.tipe.setText(items.get(i).getTipe());
            myViewHolder.sn.setText(items.get(i).getSn());
            myViewHolder.instal.setText(DateFormat.dd_mmm_yyyy(items.get(i).getInstal()));
            myViewHolder.line.setText(items.get(i).getLine());
            if("9028".equalsIgnoreCase(logo.substring(0,4))){
                myViewHolder.logo.setImageResource(R.drawable.logo_mesin_satu);
            }else if("4020".equalsIgnoreCase(logo.substring(0,4))){
                myViewHolder.logo.setImageResource(R.drawable.logo_mesin_dua);
            }else if("9040".equalsIgnoreCase(logo.substring(0,4))){
                myViewHolder.logo.setImageResource(R.drawable.logo_mesin_tiga);
            }else if("2200".equalsIgnoreCase(logo.substring(0,4))){
                myViewHolder.logo.setImageResource(R.drawable.logo_mesin_empat);
            }else if("9450".equalsIgnoreCase(logo.substring(0,4))){
                myViewHolder.logo.setImageResource(R.drawable.logo_mesin_lima);
            }else {
                myViewHolder.logo.setImageResource(R.drawable.logo_imaje);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView mm_id,tipe,sn,instal,line;
            ImageView logo;

            public MyViewHolder(View view){
                super(view);
                mm_id   = (TextView)view.findViewById(R.id.tx_mm_id);
                tipe    = (TextView)view.findViewById(R.id.tx_tipe);
                sn      = (TextView)view.findViewById(R.id.tx_sn);
                instal  = (TextView)view.findViewById(R.id.tx_instal);
                line    = (TextView)view.findViewById(R.id.tx_line);
                logo    = (ImageView)view.findViewById(R.id.img_mm_logo);

                view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View itemView) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    View view=LayoutInflater.from(context).inflate(R.layout.dialog_kontak,null);
                    alertBuilder.setView(view);
                    alertBuilder.setCancelable(true);

                    TextView tx_hapus =(TextView) view.findViewById(R.id.tx_hapus_dialogkontak);
                    TextView tx_edit  =(TextView) view.findViewById(R.id.tx_edit_dialogKontak);

                    tx_hapus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                            alertBuilder.setTitle("Konfirmasi");
                            alertBuilder.setMessage("Apakah anda yakin ingin menghapus data ini?");
                            alertBuilder.setCancelable(true);
                            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteDataMesin(items.get(getAdapterPosition()).mm_id);
                                }
                            });
                            alertBuilder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertBuilder.show();
                        }
                    });
                    tx_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogEditDataMesin("Edit Data Mesin"
                                    ,items.get(getAdapterPosition()).mm_id
                                    ,items.get(getAdapterPosition()).sn
                                    ,items.get(getAdapterPosition()).tipe
                                    ,items.get(getAdapterPosition()).line
                                    ,items.get(getAdapterPosition()).instal
                                    ,items.get(getAdapterPosition()).mm_id_pt);
                        }
                    });

                    if (id_intent.equals("data_mesin")){
                        Intent intent = new Intent(DataMesin.this,PerformaMesin.class);
                        intent.putExtra("mm_id", items.get(getAdapterPosition()).mm_id);
                        intent.putExtra("mm_tipe",items.get(getAdapterPosition()).tipe);
                        intent.putExtra("mm_sn", items.get(getAdapterPosition()).sn);
                        intent.putExtra("mm_lastPm",items.get(getAdapterPosition()).instal);
                        intent.putExtra("mm_line",items.get(getAdapterPosition()).line);
                        view.getContext().startActivity(intent);
                    }else if (id_intent.equals("pengaturan_data_mesin")) {
                        if (!sharedPreferences.getString("mu_flag","").equals("0")){
                            alertBuilder.show();
                        }
                    }

                }
            });
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////
    class Item{
        String mm_id,tipe,sn,instal,line,mm_id_pt;

        public Item(String mm_id, String tipe, String sn, String instal, String line, String mm_id_pt) {
            this.mm_id = mm_id;
            this.tipe = tipe;
            this.sn = sn;
            this.instal = instal;
            this.line = line;
            this.mm_id_pt = mm_id_pt;
        }

        public String getMm_id() {
            return mm_id;
        }

        public void setMm_id(String mm_id) {
            this.mm_id = mm_id;
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

        public String getInstal() {
            return instal;
        }

        public void setInstal(String instal) {
            this.instal = instal;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getMm_id_pt(){ return mm_id_pt; }

        public void setMm_id_pt(String mm_id_pt) {this.mm_id_pt = mm_id_pt; };
    }
}






