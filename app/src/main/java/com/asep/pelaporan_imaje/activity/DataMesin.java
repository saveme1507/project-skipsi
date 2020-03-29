package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class DataMesin extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecycleAdapter recycleAdapter;
    List<Item> items;
    ArrayList<String> data = new ArrayList<String>();
    NiceSpinner niceSpinner;
    TextView tx_judul;

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
        layoutManager   = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        items = new ArrayList<>();

        dataSpinner();
        tx_judul.setText("All");
        getDataMesin("");

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

    private void getDataMesin(String namaPT){
        String url=Server.URL + "data_mesin/select_datamesin_by_namaPT.php?nama_pt="+namaPT;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url.replace(" ","%20"),new Response.Listener<JSONArray>() {
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
                                jsonObject.getString("mm_posisi")
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

}


///////////////////////////////////////////////////////////////////////////////
class Item{
    String mm_id,tipe,sn,instal,line;

    public Item(String mm_id, String tipe, String sn, String instal, String line) {
        this.mm_id = mm_id;
        this.tipe = tipe;
        this.sn = sn;
        this.instal = instal;
        this.line = line;
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
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////
class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder>{
    List<Item> items;
    Context context;

    public RecycleAdapter(List<Item> items,Context context){
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
                public void onClick(View view) {
                    Toast.makeText(itemView.getContext(),"data"+items.get(getAdapterPosition()).sn,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}