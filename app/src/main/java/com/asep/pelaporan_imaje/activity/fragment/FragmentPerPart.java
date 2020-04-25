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

public class FragmentPerPart extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecycleAdapterPerPart recycleAdapterPerPart;
    List<ItemPerPart> itemPerParts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pergantian_part,container,false);
        recyclerView = v.findViewById(R.id.recycle_item_perPart);
        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        itemPerParts = new ArrayList<>();

        getData("1");

        return v;
    }


    public void getData(String mp_id){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "sparepart/list_perPart.php?mp_id="+mp_id, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                itemPerParts.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        FragmentPerPart.ItemPerPart myItemPerPart = new FragmentPerPart.ItemPerPart(
                                jsonObject.getString("hs_id"),
                                jsonObject.getString("hs_tgl"),
                                jsonObject.getString("mm_sn"),
                                jsonObject.getString("hs_ttd")
                        );
                        itemPerParts.add(myItemPerPart);
                        recycleAdapterPerPart = new FragmentPerPart.RecycleAdapterPerPart(itemPerParts, getActivity());
                        recycleAdapterPerPart.notifyDataSetChanged();
                        recyclerView.setAdapter(recycleAdapterPerPart);
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


    private class ItemPerPart{
        String hs_id,tgl,sn,hs_ttd;

        public ItemPerPart(String hs_id, String tgl, String sn, String hs_ttd) {
            this.hs_id = hs_id;
            this.tgl = tgl;
            this.sn = sn;
            this.hs_ttd = hs_ttd;
        }

        public String getHs_id() {
            return hs_id;
        }

        public void setHs_id(String hs_id) {
            this.hs_id = hs_id;
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

        public String getHs_ttd() {
            return hs_ttd;
        }

        public void setHs_ttd(String hs_ttd) {
            this.hs_ttd = hs_ttd;
        }
    }

    private class RecycleAdapterPerPart extends RecyclerView.Adapter<RecycleAdapterPerPart.MyViewHolderPerPart>{
        List<ItemPerPart>itemPerParts;
        Context context;

        public RecycleAdapterPerPart(List<ItemPerPart> itemPerParts, Context context) {
            this.itemPerParts = itemPerParts;
            this.context = context;
        }

        @Override
        public MyViewHolderPerPart onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_laporan,viewGroup,false);
            return  new MyViewHolderPerPart(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolderPerPart myViewHolderPerPart, int i) {
            myViewHolderPerPart.tx_tgl.setText(DateFormat.dd_MMMM_yyyy(itemPerParts.get(i).tgl));
            myViewHolderPerPart.tx_sn.setText(itemPerParts.get(i).sn);
            if (itemPerParts.get(i).hs_ttd.equals("")){
                myViewHolderPerPart.itemView.setBackgroundColor(getResources().getColor(R.color.colorlistflag));
            }
        }

        @Override
        public int getItemCount() {
            return itemPerParts.size();
        }

        public class MyViewHolderPerPart extends RecyclerView.ViewHolder{
            TextView tx_tgl,tx_sn;
            public MyViewHolderPerPart(@NonNull View itemView) {
                super(itemView);
                tx_tgl=(TextView)itemView.findViewById(R.id.tx_tgl_itemLap);
                tx_sn=(TextView)itemView.findViewById(R.id.tx_sn_itemLap);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String flag="";
                        if (! itemPerParts.get(getAdapterPosition()).hs_ttd.equals("")){
                            flag = "acc";
                        }
                        Intent intent = new Intent(getActivity(), DetailLaporan.class);
                        intent.putExtra("hlm_id",itemPerParts.get(getAdapterPosition()).hs_id);
                        intent.putExtra("flag",flag);
                        intent.putExtra("id_intent","per_part");
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
