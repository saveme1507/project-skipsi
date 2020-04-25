package com.asep.pelaporan_imaje.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.asep.pelaporan_imaje.activity.DetailLaporan;
import com.asep.pelaporan_imaje.config.DateFormat;
import com.asep.pelaporan_imaje.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentLapMesin extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecycleviewLapMesin recycleviewLapMesin ;
    List<ItemLapMesin> itemLapMesins;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laporan_mesin ,container,false);
        recyclerView = view.findViewById(R.id.recycle_item_lapMesin);
        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        itemLapMesins = new ArrayList<>();

        getData("1");

        return view;

    }

    public void getData(String mp_id){
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "buat_lapPengerjaan/list_lapMesin.php?mp_id="+mp_id, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                itemLapMesins.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        FragmentLapMesin.ItemLapMesin myItemLapMesins = new ItemLapMesin(
                                jsonObject.getString("hlm_id"),
                                jsonObject.getString("hlm_tanggal"),
                                jsonObject.getString("mm_sn"),
                                jsonObject.getString("hlm_ttd")
                        );
                        itemLapMesins.add(myItemLapMesins);
                        recycleviewLapMesin = new FragmentLapMesin.RecycleviewLapMesin(itemLapMesins, getActivity());
                        recycleviewLapMesin.notifyDataSetChanged();
                        recyclerView.setAdapter(recycleviewLapMesin);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }


    private class ItemLapMesin{
        String hlm_id,tgl,sn,hlm_ttd;

        public ItemLapMesin(String hlm_id, String tgl, String sn, String hlm_ttd) {
            this.hlm_id=hlm_id;
            this.tgl = tgl;
            this.sn = sn;
            this.hlm_ttd = hlm_ttd;
        }

        public String getHlm_id() {
            return hlm_id;
        }

        public void setHlm_id(String hlm_id) {
            this.hlm_id = hlm_id;
        }

        public String getTgl() {
            return tgl;
        }

        public void setTgl(String tgl) {
            this.tgl = tgl;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getHlm_ttd() {
            return hlm_ttd;
        }

        public void setHlm_ttd(String hlm_ttd) {
            this.hlm_ttd = hlm_ttd;
        }
    }

    private class RecycleviewLapMesin extends RecyclerView.Adapter<RecycleviewLapMesin.MyViewHolderLapMesin>{
         List<ItemLapMesin> itemLapMesins;
        Context context;

        public RecycleviewLapMesin(List<ItemLapMesin> itemLapMesins, Context context) {
            this.itemLapMesins = itemLapMesins;
            this.context = context;
        }

        @Override
        public MyViewHolderLapMesin onCreateViewHolder( ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_laporan,viewGroup,false);
            return  new MyViewHolderLapMesin(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleviewLapMesin.MyViewHolderLapMesin myViewHolderLapMesin, int i) {
            myViewHolderLapMesin.tx_tgl.setText(DateFormat.dd_MMMM_yyyy(itemLapMesins.get(i).tgl));
            myViewHolderLapMesin.tx_sn.setText(itemLapMesins.get(i).sn);
            if (itemLapMesins.get(i).hlm_ttd.equals("")){
                myViewHolderLapMesin.itemView.setBackgroundColor(getResources().getColor(R.color.colorlistflag));
            }
        }

        @Override
        public int getItemCount() {
            return itemLapMesins.size();
        }

        public class MyViewHolderLapMesin extends RecyclerView.ViewHolder{
            TextView tx_tgl,tx_sn;
            public MyViewHolderLapMesin(@NonNull View itemView) {
                super(itemView);
                tx_tgl=(TextView)itemView.findViewById(R.id.tx_tgl_itemLap);
                tx_sn=(TextView)itemView.findViewById(R.id.tx_sn_itemLap);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String flag="";
                        if (! itemLapMesins.get(getAdapterPosition()).hlm_ttd.equals("")){
                            flag = "acc";
                        }
                        Intent intent = new Intent(getActivity(), DetailLaporan.class);
                        intent.putExtra("hlm_id",itemLapMesins.get(getAdapterPosition()).hlm_id);
                        intent.putExtra("flag",flag);
                        intent.putExtra("id_intent","lap_mesin");
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
