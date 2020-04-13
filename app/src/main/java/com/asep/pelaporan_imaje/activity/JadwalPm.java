package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class JadwalPm extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    RecyclerView recyclerViewJadwalpm;
    RecyclerView.LayoutManager layoutManagerJadwalpm;
    RecycleAdapterJadwalpm recycleAdapterJadwalpm;
    List<ItemJadwalpm> itemJadwalpms;
    NiceSpinner niceSpinner;
    private ArrayList<String> data = new ArrayList<>();
    private TextView tx_judul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_pm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_jadwalpm);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);

        recyclerViewJadwalpm = (RecyclerView)findViewById(R.id.recycle_item_jadwalpm);
        niceSpinner = (NiceSpinner)findViewById(R.id.spinner_sort_Jadwalpm);
        tx_judul    = (TextView)findViewById(R.id.tx_judul_jadwalpm);
        layoutManagerJadwalpm = new LinearLayoutManager(this);
        recyclerViewJadwalpm.setLayoutManager(layoutManagerJadwalpm);
        recyclerViewJadwalpm.setHasFixedSize(true);
        itemJadwalpms = new ArrayList<>();


        if (sharedPreferences.getString("mu_flag","").equals("0")){
            tx_judul.setText(sharedPreferences.getString("mp_nama",""));
            niceSpinner.setVisibility(View.GONE);
        }else{
            tx_judul.setText("All");
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
                                jsonObject.getString("mm_last_pm")
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    class ItemJadwalpm{
        String mm_id,mm_sn,mm_tipe,mm_last_pm;

        public ItemJadwalpm(String mm_id, String mm_sn, String mm_tipe, String mm_last_pm) {
            this.mm_id = mm_id;
            this.mm_sn = mm_sn;
            this.mm_tipe = mm_tipe;
            this.mm_last_pm = mm_last_pm;
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
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    class RecycleAdapterJadwalpm extends RecyclerView.Adapter<RecycleAdapterJadwalpm.MyViewHolderJadwalpm>{
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
