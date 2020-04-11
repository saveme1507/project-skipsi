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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.config.DateFormat;
import com.asep.pelaporan_imaje.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Pelaporan extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Pelaporan.RecycleAdapterPelaporan recycleAdapterPelaporan;
    List<Pelaporan.ItemPelaporan> itemPelaporans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelaporan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_pelaporan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recyclerView =(RecyclerView)findViewById(R.id.recycle_item_pelaporan);
        layoutManager   = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        selectPelaporan("");
    }

    private void selectPelaporan(String status){
        String url= Server.URL + "buat_laporan/select_pelaporan_by_status.php?status="+status;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url.replace(" ","%20"),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response :",response.toString());
                itemPelaporans = new ArrayList<>();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        ItemPelaporan myitemPelaporan = new ItemPelaporan(
                                jsonObject.getString("lk_id"),
                                jsonObject.getString("lk_tgl"),
                                jsonObject.getString("lk_ket"),
                                jsonObject.getString("lk_status"),
                                jsonObject.getString("lk_update"),
                                jsonObject.getString("hlm_id"),
                                jsonObject.getString("mu_id"),
                                jsonObject.getString("mu_nama"),
                                jsonObject.getString("mp_id"),
                                jsonObject.getString("mp_nama")
                        );
                        itemPelaporans.add(myitemPelaporan);
                        recycleAdapterPelaporan  = new RecycleAdapterPelaporan(itemPelaporans,Pelaporan.this);
                        recycleAdapterPelaporan.notifyDataSetChanged();
                        recyclerView.setAdapter(recycleAdapterPelaporan);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ItemPelaporan{
        String lk_id,lk_tgl,lk_ket,lk_status,lk_update,hlm_id,mu_id,mu_nama,mp_id,mp_nama;

        public ItemPelaporan(String lk_id, String lk_tgl, String lk_ket, String lk_status, String lk_update, String hlm_id, String mu_id, String mu_nama, String mp_id, String mp_nama) {
            this.lk_id = lk_id;
            this.lk_tgl = lk_tgl;
            this.lk_ket = lk_ket;
            this.lk_status = lk_status;
            this.lk_update = lk_update;
            this.hlm_id = hlm_id;
            this.mu_id = mu_id;
            this.mu_nama = mu_nama;
            this.mp_id = mp_id;
            this.mp_nama = mp_nama;
        }

        public String getLk_id() {
            return lk_id;
        }

        public void setLk_id(String lk_id) {
            this.lk_id = lk_id;
        }

        public String getLk_tgl() {
            return lk_tgl;
        }

        public void setLk_tgl(String lk_tgl) {
            this.lk_tgl = lk_tgl;
        }

        public String getLk_ket() {
            return lk_ket;
        }

        public void setLk_ket(String lk_ket) {
            this.lk_ket = lk_ket;
        }

        public String getLk_status() {
            return lk_status;
        }

        public void setLk_status(String lk_status) {
            this.lk_status = lk_status;
        }

        public String getLk_update() {
            return lk_update;
        }

        public void setLk_update(String lk_update) {
            this.lk_update = lk_update;
        }

        public String getHlm_id() {
            return hlm_id;
        }

        public void setHlm_id(String hlm_id) {
            this.hlm_id = hlm_id;
        }

        public String getMu_id() {
            return mu_id;
        }

        public void setMu_id(String mu_id) {
            this.mu_id = mu_id;
        }

        public String getMu_nama() {
            return mu_nama;
        }

        public void setMu_nama(String mu_nama) {
            this.mu_nama = mu_nama;
        }

        public String getMp_id() {
            return mp_id;
        }

        public void setMp_id(String mp_id) {
            this.mp_id = mp_id;
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
    class RecycleAdapterPelaporan extends RecyclerView.Adapter<RecycleAdapterPelaporan.MyViewHolderPelaporan>{
        List<ItemPelaporan> itemPelaporans;
        Context context;

        public RecycleAdapterPelaporan(List<ItemPelaporan> itemPelaporans, Context context) {
            this.itemPelaporans = itemPelaporans;
            this.context = context;
        }

        @Override
        public RecycleAdapterPelaporan.MyViewHolderPelaporan onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pelaporan,viewGroup,false);
            return  new RecycleAdapterPelaporan.MyViewHolderPelaporan(view);
        }

        @Override
        public void onBindViewHolder(RecycleAdapterPelaporan.MyViewHolderPelaporan myViewHolderPelaporan, int i) {
            myViewHolderPelaporan.tx_tgl.setText("Tanggal "+DateFormat.dateTimeTanggal(itemPelaporans.get(i).lk_tgl));
            myViewHolderPelaporan.tx_mpNama_muNama.setText(itemPelaporans.get(i).mp_nama+" | "+itemPelaporans.get(i).mu_nama);
            myViewHolderPelaporan.tx_des.setText(itemPelaporans.get(i).lk_ket);
            myViewHolderPelaporan.tx_lkStatus_lkUpdate.setText("Status: "+ itemPelaporans.get(i).lk_status+" | "+DateFormat.dateTimeStatus(itemPelaporans.get(i).lk_update));
        }

        @Override
        public int getItemCount() {
            return itemPelaporans.size();
        }

        public class MyViewHolderPelaporan extends RecyclerView.ViewHolder{
            TextView tx_tgl,tx_mpNama_muNama,tx_des,tx_lkStatus_lkUpdate;
            public MyViewHolderPelaporan(View itemView) {
                super(itemView);
                tx_tgl          =(TextView)itemView.findViewById(R.id.tx_tgl_itemPelaporan);
                tx_mpNama_muNama=(TextView)itemView.findViewById(R.id.tx_pt_user_itemPelaporan);
                tx_des          =(TextView)itemView.findViewById(R.id.tx_deskripsi_itemPelaporan);
                tx_lkStatus_lkUpdate =(TextView)itemView.findViewById(R.id.tx_status_update_itemPelaporan);

            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
