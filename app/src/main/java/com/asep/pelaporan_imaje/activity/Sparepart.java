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
import com.asep.pelaporan_imaje.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Sparepart extends AppCompatActivity {
    RecyclerView recyclerViewSparepart;
    RecyclerView.LayoutManager layoutManagerSparepart;
    RecycleAdapterSparepart recycleAdapterSparepart;
    List<ItemSparepart> itemsSparepart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sparepart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_master_sparepart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recyclerViewSparepart    = (RecyclerView)findViewById(R.id.recycle_item_sparepart);
        layoutManagerSparepart   = new LinearLayoutManager(this);
        recyclerViewSparepart.setLayoutManager(layoutManagerSparepart);
        recyclerViewSparepart.setHasFixedSize(true);
        itemsSparepart = new ArrayList<>();

        getDataSparepart();
    }

    private void getDataSparepart(){
        String url = Server.URL+"sparepart/select_spapepart.php";
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response :",response.toString());
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        ItemSparepart myitemSparepart = new ItemSparepart(
                                jsonObject.getString("mf_nama"),
                                jsonObject.getString("mf_path")
                        );
                        itemsSparepart.add(myitemSparepart);
                        recycleAdapterSparepart  = new RecycleAdapterSparepart(itemsSparepart,Sparepart.this);
//                        recycleAdapterSparepart.notifyDataSetChanged();
                        recyclerViewSparepart.setAdapter(recycleAdapterSparepart);
                    }catch (JSONException e){
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
}


///////////////////////////////////////////////////////////////////////////////////////////////////
class ItemSparepart{
    String nama,patch;

    public ItemSparepart(String nama, String patch) {
        this.nama = nama;
        this.patch = patch;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPatch() {
        return patch;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }
}


////////////////////////////////////////////////////////////////////////////////////////////////////
class RecycleAdapterSparepart extends RecyclerView.Adapter<RecycleAdapterSparepart.MyViewHolderSparepart>{
    List<ItemSparepart> itemSpareparts;
    Context context;

    public RecycleAdapterSparepart(List<ItemSparepart> itemSpareparts, Context context) {
        this.itemSpareparts = itemSpareparts;
        this.context = context;
    }

    @Override
    public MyViewHolderSparepart onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sparepart,viewGroup,false);
        return new MyViewHolderSparepart(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolderSparepart myViewHolderSparepart, int i) {
        myViewHolderSparepart.nama.setText(itemSpareparts.get(i).nama);
        myViewHolderSparepart.patch.setText(itemSpareparts.get(i).patch);


    }

    @Override
    public int getItemCount() {
        return itemSpareparts.size();
    }

    public class MyViewHolderSparepart extends RecyclerView.ViewHolder{
        TextView nama,patch;

        public MyViewHolderSparepart(View view){
            super(view);
            nama    = (TextView)view.findViewById(R.id.tx_nama_sparepart);
            patch   = (TextView)view.findViewById(R.id.tx_path_sparepart);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),DetailSparepart.class);
                    intent.putExtra("nama",itemSpareparts.get(getAdapterPosition()).nama);
                    intent.putExtra("patch",itemSpareparts.get(getAdapterPosition()).patch);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}