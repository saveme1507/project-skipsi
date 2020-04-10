package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JadwalKunjungan extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerViewJadwalKunjungan;
    RecyclerView.LayoutManager layoutManagerJadwalKunjungan;
    RecycleAdapterJadwalKunjungan recycleAdapterJadwalKunjungan;
    List<ItemJadwalKunjungan> itemJadwalKunjungans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context mContext = getApplicationContext();
        setContentView(R.layout.activity_jadwal_kunjungan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_jadwalKunjungan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        refreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipRefresh_jadwalKunjungan);
        layoutManagerJadwalKunjungan = new LinearLayoutManager(this);
        recyclerViewJadwalKunjungan =(RecyclerView)findViewById(R.id.recycle_item_jadwalKunjungan);
        recyclerViewJadwalKunjungan.setLayoutManager(layoutManagerJadwalKunjungan);
        recyclerViewJadwalKunjungan.setHasFixedSize(true);
        itemJadwalKunjungans = new ArrayList<>();

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getData();
            }
        });
    }

    public void getData(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "jadwal_kunjungan/select_data.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                itemJadwalKunjungans.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        ItemJadwalKunjungan myitemJadwalKunjungan = new ItemJadwalKunjungan(
                                jsonObject.getString("mj_hari"),
                                jsonObject.getString("mj_pelanggan_1"),
                                jsonObject.getString("mj_pelanggan_2")
                        );
                        itemJadwalKunjungans.add(myitemJadwalKunjungan);
                        recycleAdapterJadwalKunjungan = new RecycleAdapterJadwalKunjungan(itemJadwalKunjungans, JadwalKunjungan.this);
                        recycleAdapterJadwalKunjungan.notifyDataSetChanged();
                        recyclerViewJadwalKunjungan.setAdapter(recycleAdapterJadwalKunjungan);
                        refreshLayout.setRefreshing(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        refreshLayout.setRefreshing(false);
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

    public void updateData(final String hari, final String pel1, final String pel2){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"jadwal_kunjungan/update_data.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        getData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Terjadi kesalahan edit jadwal",Toast.LENGTH_SHORT).show();
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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mj_hari", hari);
                params.put("mj_pel1", pel1);
                params.put("mj_pel2", pel2);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    @Override
    public void onRefresh() {
        getData();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    class RecycleAdapterJadwalKunjungan extends RecyclerView.Adapter<RecycleAdapterJadwalKunjungan.MyViewHolderJadwalKunjungan>{
        List<ItemJadwalKunjungan> itemJadwalKunjungans;
        Context context;

        public RecycleAdapterJadwalKunjungan(List<ItemJadwalKunjungan> itemJadwalKunjungans, Context context) {
            this.itemJadwalKunjungans = itemJadwalKunjungans;
            this.context = context;
        }

        @Override
        public MyViewHolderJadwalKunjungan onCreateViewHolder( ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_jadwal_kunjungan,viewGroup,false);
            return  new MyViewHolderJadwalKunjungan(view);
        }

        @Override
        public void onBindViewHolder( RecycleAdapterJadwalKunjungan.MyViewHolderJadwalKunjungan myViewHolderJadwalKunjungan, int i) {
            myViewHolderJadwalKunjungan.tx_hari.setText(itemJadwalKunjungans.get(i).hari);
            myViewHolderJadwalKunjungan.tx_pel1.setText(itemJadwalKunjungans.get(i).pel_1);
            myViewHolderJadwalKunjungan.tx_pel2.setText(itemJadwalKunjungans.get(i).pel_2);
        }

        @Override
        public int getItemCount() {
            return itemJadwalKunjungans.size();
        }
        public class MyViewHolderJadwalKunjungan extends RecyclerView.ViewHolder {
            TextView tx_hari,tx_pel1,tx_pel2;
            public MyViewHolderJadwalKunjungan(View itemView) {
                super(itemView);
                tx_hari =(TextView)itemView.findViewById(R.id.tx_hari_item_jadwalKunjungan);
                tx_pel1 =(TextView)itemView.findViewById(R.id.tx_pelanggan1_item_jadwalKunjungan);
                tx_pel2 =(TextView)itemView.findViewById(R.id.tx_pelanggan2_item_jadwalKunjungan);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View itemView) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        View view=LayoutInflater.from(context).inflate(R.layout.dialog_edit_jadwalkunjungan,null);
                        alertBuilder.setView(view);
                        alertBuilder.setCancelable(false);

                        TextView tx_judul =(TextView) view.findViewById(R.id.tx_judul_dialogJadwalkunjungan);
                        final TextView tx_pel1  =(TextView) view.findViewById(R.id.et_pel1_dialogJadwalkunjungan);
                        final TextView tx_pel2  =(TextView) view.findViewById(R.id.et_pel2_dialogJadwalkunjungan);

                        String kapital = itemJadwalKunjungans.get(getAdapterPosition()).hari.substring(0,1).toUpperCase();
                        tx_judul.setText("Kunjungan Hari "+itemJadwalKunjungans.get(getAdapterPosition()).hari.replace(itemJadwalKunjungans.get(getAdapterPosition()).hari.substring(0,1),kapital));
                        alertBuilder.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String pel1 = tx_pel1.getText().toString();
                                String pel2 = tx_pel2.getText().toString();
                                updateData(itemJadwalKunjungans.get(getAdapterPosition()).hari, pel1, pel2);

                            }
                        }).setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertBuilder.show();
                    }
                });
            }
        }
    }
}
