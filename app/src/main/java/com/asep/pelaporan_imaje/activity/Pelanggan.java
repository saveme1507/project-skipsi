package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.asep.pelaporan_imaje.config.BitmapFormat;
import com.asep.pelaporan_imaje.server.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pelanggan extends AppCompatActivity {
    RecyclerView recyclerViewPelangan;
    RecyclerView.LayoutManager layoutManagerPelanggan;
    List<ItemPelanggan> itemPelanggans = new ArrayList<>();
    RecycleAdapterPelanggan recycleAdapterPelanggan;
    private static final int PICK_IMAGE_REQUEST =1 ;
    int bitmap_size = 60; // range 1 - 100
    Bitmap bitmap, decoded;

    AlertDialog.Builder alertBuilder;
    LayoutInflater layoutInflater;
    View view;
    ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_pelanggan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        FloatingActionButton floatingActionButton =(FloatingActionButton)findViewById(R.id.floating_tambah_pelanggan);
        recyclerViewPelangan =(RecyclerView)findViewById(R.id.recycle_item_pelanggan);
        layoutManagerPelanggan = new LinearLayoutManager(this);
        recyclerViewPelangan.setLayoutManager(layoutManagerPelanggan);
        recyclerViewPelangan.setHasFixedSize(true);

        selectDataPelanggan();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditPelanggan("","","","");
            }
        });
    }
    private void selectDataPelanggan(){
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL+"pelanggan/select_datapelanggan.php",new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itemPelanggans.clear();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Pelanggan.ItemPelanggan myitem = new Pelanggan.ItemPelanggan(
                                jsonObject.getString("mp_id"),
                                jsonObject.getString("mp_nama"),
                                jsonObject.getString("mp_alamat"),
                                jsonObject.getString("mp_logo")
                        );
                        itemPelanggans.add(myitem);
                        recycleAdapterPelanggan  = new Pelanggan.RecycleAdapterPelanggan(itemPelanggans,Pelanggan.this);
                        recycleAdapterPelanggan.notifyDataSetChanged();
                        recyclerViewPelangan.setAdapter(recycleAdapterPelanggan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error response: ", error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    private void updatePelanggan(final String id, final String nama, final String alamat, final String logo){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"pelanggan/update_datapelanggan.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        selectDataPelanggan();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Edit data pelanggan gagal",Toast.LENGTH_SHORT).show();
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
                params.put("mp_id", id);
                params.put("mp_nama", nama);
                params.put("mp_alamat", alamat);
                params.put("mp_logo",getStringImage(BitmapFormat.getBitmapFromImageView(iv_logo)));
                params.put("patch_logo", logo.replace(
                        Server.URL+"pelanggan/images_pelanggan/",
                        "/home/artisanc/public_html/app-pelaporan/pelaporan_imaje/pelanggan/images_pelanggan/"));
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void insertPelanggan(final String nama, final String alamat, final String logo){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"pelanggan/insert_datapelanggan.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        selectDataPelanggan();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Tambah data pelanggan gagal",Toast.LENGTH_SHORT).show();
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
                params.put("mp_nama", nama );
                params.put("mp_alamat", alamat);
                params.put("mp_logo", getStringImage(decoded));
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void deletePelanggan(final String id, final String logo){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"pelanggan/delete_datapelanggan.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        selectDataPelanggan();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Hapus data pelanggan gagal",Toast.LENGTH_SHORT).show();
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
                params.put("mp_id", id);
                params.put("patch_logo", logo.replace(
                        Server.URL+"pelanggan/images_pelanggan/",
                        "/home/artisanc/public_html/app-pelaporan/pelaporan_imaje/pelanggan/images_pelanggan/"));

//                "C:\\xampp\\htdocs\\pelaporan_imaje\\pelanggan\\images_pelanggan\\"
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    private void dialogEditPelanggan(final String id, String nama, String alamat, final String logo){
        alertBuilder = new AlertDialog.Builder(Pelanggan.this);
        layoutInflater = getLayoutInflater();
        view = layoutInflater.inflate(R.layout.dialog_edit_pelanggan,null);
        alertBuilder.setView(view);
        alertBuilder.setCancelable(false);

        final TextView tx_judul   =(TextView)view.findViewById(R.id.tx_judul_dialogpelanggan);
        final TextView tx_id      =(TextView)view.findViewById(R.id.tx_id_dialogpelanggan);
        final EditText tx_nama    =(EditText) view.findViewById(R.id.et_nama_dialogpelanggan);
        final EditText tx_alamat  =(EditText) view.findViewById(R.id.et_alamat_dialogpelanggan);
        iv_logo    =(ImageView)view.findViewById(R.id.iv_logopelanggan_dialogpelanggan);
        Button bt_pilihlogo =(Button)view.findViewById(R.id.bt_pilihlog_dialogpelanggan);
        if (!id.equals("")){
            tx_judul.setText("Edit Data Pelanggan");
            tx_id.setText(id);
            tx_nama.setText(nama);
            tx_alamat.setText(alamat);
            if (!logo.equals("")) {
                Picasso.get()
                        .load(logo)
                        .error(R.drawable.icon_item_pelanggan)
//                        .centerCrop()
//                        .fit()
                        .into(iv_logo);
            }else{
                iv_logo.setImageResource(R.drawable.icon_item_pelanggan);
            }
        }else{
            tx_judul.setText("Tambah Data Pelanggan");
        }
        bt_pilihlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        alertBuilder.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (iv_logo.getDrawable()!=null && tx_nama.getText().toString().trim().length()>1 && tx_nama.getText().toString().trim().length()>1 ){
                    if (id.equals("")){
                        insertPelanggan(tx_nama.getText().toString(),
                                tx_alamat.getText().toString(),
                                "");
                    }else{
                        updatePelanggan(tx_id.getText().toString(),
                                tx_nama.getText().toString(),
                                tx_alamat.getText().toString(),
                                logo);
                    }
                }else {
                    Toast.makeText(Pelanggan.this,"Kolom dan Logo tidak boleh kosong!",Toast.LENGTH_SHORT).show();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecycleAdapterPelanggan extends RecyclerView.Adapter<RecycleAdapterPelanggan.MyViewHolderPelanggan>{
        List<ItemPelanggan> itemPelanggans;
        Context context;

        public RecycleAdapterPelanggan(List<ItemPelanggan> itemPelanggans, Context context) {
            this.itemPelanggans = itemPelanggans;
            this.context = context;
        }

        @Override
        public MyViewHolderPelanggan onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pelanggan,viewGroup,false);
            return  new MyViewHolderPelanggan(view);
        }

        @Override
        public void onBindViewHolder(RecycleAdapterPelanggan.MyViewHolderPelanggan myViewHolderPelanggan, int i) {
            myViewHolderPelanggan.tx_nama.setText(itemPelanggans.get(i).nama);
            myViewHolderPelanggan.tx_alamat.setText(itemPelanggans.get(i).alamat);
            String logo = itemPelanggans.get(i).logo;
            if (!logo.equals("")) {
                Picasso.get()
                        .load(itemPelanggans.get(i).logo)
                        .error(R.drawable.icon_item_pelanggan)
                        .centerCrop()
                        .fit()
                        .into(myViewHolderPelanggan.iv_logo);
            }else{
                myViewHolderPelanggan.iv_logo.setImageResource(R.drawable.icon_item_pelanggan);
            }
        }
        @Override
        public int getItemCount() {
            return itemPelanggans.size();
        }

        public  class MyViewHolderPelanggan extends RecyclerView.ViewHolder{
            TextView tx_nama,tx_alamat;
            ImageView iv_logo;
            public MyViewHolderPelanggan(View itemView) {
                super(itemView);
                tx_nama =(TextView)itemView.findViewById(R.id.tx_nama_pelanggan);
                tx_alamat =(TextView)itemView.findViewById(R.id.tx_alamat_pelanggan);
                iv_logo =(ImageView)itemView.findViewById(R.id.iv_logo_pelanggan);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewOnklick) {
                        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        View view=LayoutInflater.from(context).inflate(R.layout.dialog_kontak,null);
                        alertBuilder.setView(view);
                        alertBuilder.setCancelable(true);

                        TextView tx_hapus =(TextView) view.findViewById(R.id.tx_hapus_dialogkontak);
                        TextView tx_edit  =(TextView) view.findViewById(R.id.tx_edit_dialogKontak);

                        final DialogInterface dialogInterface12 = null;
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
                                        deletePelanggan(itemPelanggans.get(getAdapterPosition()).id,itemPelanggans.get(getAdapterPosition()).logo);
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
                                dialogEditPelanggan(
                                        itemPelanggans.get(getAdapterPosition()).id,
                                        itemPelanggans.get(getAdapterPosition()).nama,
                                        itemPelanggans.get(getAdapterPosition()).alamat,
                                        itemPelanggans.get(getAdapterPosition()).logo);
                            }
                        });
                        alertBuilder.show();
                    }
                });


            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ItemPelanggan{
        String id,nama,alamat,logo;

        public ItemPelanggan(String id, String nama, String alamat, String logo) {
            this.id = id;
            this.nama = nama;
            this.alamat = alamat;
            this.logo = logo;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public String getAlamat() {
            return alamat;
        }

        public void setAlamat(String alamat) {
            this.alamat = alamat;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        iv_logo.setImageBitmap(decoded);
    }
    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
