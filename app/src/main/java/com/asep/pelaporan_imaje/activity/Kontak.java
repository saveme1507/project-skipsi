package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class Kontak extends AppCompatActivity {
    RecyclerView recyclerViewKontak;
    RecyclerView.LayoutManager layoutManagerKontak;
    RecycleAdapterKontak recycleAdapterKontak;
    List<ItemKontak> itemKontaks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontak);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_kontak);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        layoutManagerKontak = new LinearLayoutManager(this);
        recyclerViewKontak =(RecyclerView)findViewById(R.id.recycle_item_kontak);
        recyclerViewKontak.setLayoutManager(layoutManagerKontak);
        recyclerViewKontak.setHasFixedSize(true);
        itemKontaks = new ArrayList<>();
        getDataKontak();
    }
    private void getDataKontak(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "kontak/get_kontak.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        ItemKontak myitemKontak = new ItemKontak(
                                jsonObject.getString("mu_nama"),
                                jsonObject.getString("mu_flag"),
                                jsonObject.getString("mu_telp"),
                                jsonObject.getString("mu_email")
                        );
                        itemKontaks.add(myitemKontak);
                        recycleAdapterKontak = new RecycleAdapterKontak (itemKontaks, Kontak.this);
                        recyclerViewKontak.setAdapter(recycleAdapterKontak);
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    class ItemKontak{
        String mu_nama,mu_flag,mu_telp,mu_email;

        public ItemKontak(String mu_nama, String mu_flag, String mu_telp, String mu_email) {
            this.mu_nama = mu_nama;
            this.mu_flag = mu_flag;
            this.mu_telp = mu_telp;
            this.mu_email = mu_email;
        }

        public String getMu_nama() {
            return mu_nama;
        }

        public void setMu_nama(String mu_nama) {
            this.mu_nama = mu_nama;
        }

        public String getMu_flag() {
            return mu_flag;
        }

        public void setMu_flag(String mu_flag) {
            this.mu_flag = mu_flag;
        }

        public String getMu_telp() {
            return mu_telp;
        }

        public void setMu_telp(String mu_telp) {
            this.mu_telp = mu_telp;
        }

        public String getMu_email() {
            return mu_email;
        }

        public void setMu_email(String mu_email) {
            this.mu_email = mu_email;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    class RecycleAdapterKontak extends RecyclerView.Adapter<RecycleAdapterKontak.MyViewHolderKontak>{
        List<ItemKontak> itemKontaks;
        Context context;

        public RecycleAdapterKontak(List<ItemKontak> itemKontaks, Context context) {
            this.itemKontaks = itemKontaks;
            this.context = context;
        }

        @Override
        public MyViewHolderKontak onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kontak,viewGroup,false);
            return  new MyViewHolderKontak(view);
        }

        @Override
        public void onBindViewHolder(RecycleAdapterKontak.MyViewHolderKontak myViewHolderKontak, int i) {
            String kapital  = itemKontaks.get(i).mu_nama.substring(0,1).toUpperCase();
            String nama     = itemKontaks.get(i).mu_nama.replace(itemKontaks.get(i).mu_nama.substring(0,1),kapital);
            myViewHolderKontak.tx_nama.setText(nama);

            String jabatan = null;
            int flag = Integer.parseInt((itemKontaks.get(i).mu_flag));
            if (flag==1){
                jabatan="Teknisi";
            }else if (flag==2){
                jabatan="Assitent Supervisor";
            }else if (flag==3){
                jabatan="Supervisor";
            }else if (flag==4){
                jabatan="Branch Manager";
            }else if (flag==5){
                jabatan="Manager Teknik";
            }else if (flag==6){
                jabatan="Owner";
            }
            myViewHolderKontak.tx_jabatan.setText(jabatan);
            myViewHolderKontak.tx_telp.setText(itemKontaks.get(i).mu_telp);
        }

        @Override
        public int getItemCount() {
            return itemKontaks.size();
        }

        public class MyViewHolderKontak extends RecyclerView.ViewHolder{
            TextView tx_nama,tx_jabatan,tx_telp;
            ImageView iv_telp,iv_wa,iv_email;

            public MyViewHolderKontak(View itemView) {
                super(itemView);
                tx_nama = (TextView)itemView.findViewById(R.id.tx_nama_itemkontak);
                tx_jabatan = (TextView)itemView.findViewById(R.id.tx_jabatan_itemkontak);
                tx_telp = (TextView)itemView.findViewById(R.id.tx_telp_itemkontak);
                iv_telp =(ImageView)itemView.findViewById(R.id.iv_telp_kontak);
                iv_wa   =(ImageView)itemView.findViewById(R.id.iv_wa_kontak);
                iv_email=(ImageView)itemView.findViewById(R.id.iv_email_kontak);

                iv_telp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent telpon = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+itemKontaks.get(getAdapterPosition()).mu_telp));
                        view.getContext().startActivity(telpon);
                    }
                });
                iv_wa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = "https://api.whatsapp.com/send?phone=+62"+itemKontaks.get(getAdapterPosition()).mu_telp.substring(1,11);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                iv_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        String[] strTo = { itemKontaks.get(getAdapterPosition()).mu_email };
                        intent.putExtra(Intent.EXTRA_EMAIL, strTo);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Pelaporan gangguan mesin barcode");
                        intent.putExtra(Intent.EXTRA_TEXT, " ");
//                        Uri attachments = Uri.parse(image_path);
//                        intent.putExtra(Intent.EXTRA_STREAM, attachments);
                        intent.setType("message/rfc822");
                        intent.setPackage("com.google.android.gm");
                        startActivity(intent);
                    }
                });
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////

}