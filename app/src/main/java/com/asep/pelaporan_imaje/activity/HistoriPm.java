package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.Intent;
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

public class HistoriPm extends AppCompatActivity {
    RecyclerView recyclerViewHistoripm;
    RecyclerView.LayoutManager layoutManagerHistoripm;
    RecycleAdapterHistoripm recycleAdapterHistoripm;
    List<ItemHistorpm> itemHistorpms;
    String mmId, tipeSn, lastPm, nextPm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori_pm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_historipm);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        layoutManagerHistoripm = new LinearLayoutManager(this);
        recyclerViewHistoripm = (RecyclerView) findViewById(R.id.recycle_item_historipm);
        recyclerViewHistoripm.setLayoutManager(layoutManagerHistoripm);
        recyclerViewHistoripm.setHasFixedSize(true);
        itemHistorpms = new ArrayList<>();

        lastPm = getIntent().getStringExtra("last_pm");
        nextPm = getIntent().getStringExtra("next_pm");
        tipeSn = getIntent().getStringExtra("tipe-sn");
        mmId = getIntent().getStringExtra("mm_id");
        getDatalist();

        TextView tipe_sn = (TextView) findViewById(R.id.tx_tipe_sn_historipm);
        TextView last_pm = (TextView) findViewById(R.id.tx_lastpm_historipm);
        TextView next_pm = (TextView) findViewById(R.id.tx_nextpm_historipm);
        tipe_sn.setText(tipeSn);
        last_pm.setText(lastPm);
        next_pm.setText(nextPm);

    }

    private void getDatalist() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "jadwalpm/select_historipm.php?id_mesin=" + mmId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        ItemHistorpm myitemHistorpm = new ItemHistorpm(
                                jsonObject.getString("hlm_id"),
                                jsonObject.getString("hlm_tanggal")
                        );
                        itemHistorpms.add(myitemHistorpm);
                        recycleAdapterHistoripm = new RecycleAdapterHistoripm(itemHistorpms, HistoriPm.this);
                        recyclerViewHistoripm.setAdapter(recycleAdapterHistoripm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    class ItemHistorpm{
        String hlm_id,hlm_tanggal;

        public ItemHistorpm(String hlm_id, String hlm_tanggal) {
            this.hlm_id = hlm_id;
            this.hlm_tanggal = hlm_tanggal;
        }

        public String getHlm_id() {
            return hlm_id;
        }

        public void setHlm_id(String hlm_id) {
            this.hlm_id = hlm_id;
        }

        public String getHlm_tanggal() {
            return hlm_tanggal;
        }

        public void setHlm_tanggal(String hlm_tanggal) {
            this.hlm_tanggal = hlm_tanggal;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    class RecycleAdapterHistoripm extends RecyclerView.Adapter<RecycleAdapterHistoripm.MyViewHolderHistoripm>{
        List<ItemHistorpm> itemHistorpms;
        Context context;

        public RecycleAdapterHistoripm(List<ItemHistorpm> itemHistorpms, Context context) {
            this.itemHistorpms = itemHistorpms;
            this.context = context;
        }

        @Override
        public MyViewHolderHistoripm onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_historipm,viewGroup,false);
            return new MyViewHolderHistoripm(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolderHistoripm myViewHoldelHistoripm, int i) {
            myViewHoldelHistoripm.hlm_id.setText(itemHistorpms.get(i).hlm_id);
            myViewHoldelHistoripm.hlm_tanggal.setText(DateFormat.dd_MMMM_yyyy(itemHistorpms.get(i).hlm_tanggal));
        }

        @Override
        public int getItemCount() {
            return itemHistorpms.size();
        }

        public class MyViewHolderHistoripm extends RecyclerView.ViewHolder{
            TextView hlm_id,hlm_tanggal;
            public MyViewHolderHistoripm(View itemView) {
                super(itemView);
                hlm_id = (TextView)itemView.findViewById(R.id.tx_id_itemhistori);
                hlm_tanggal = (TextView)itemView.findViewById(R.id.tx_tanggal_itemhistori);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HistoriPm.this,DetailLaporan.class);
                        intent.putExtra("id_intent","lap_mesin");
                        intent.putExtra("hlm_id",itemHistorpms.get(getAdapterPosition()).hlm_id);
                        intent.putExtra("flag","");
                        startActivity(intent);
//                    Intent intent = new Intent(view.getContext(),DetailSparepart.class);
//                    view.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////

}

