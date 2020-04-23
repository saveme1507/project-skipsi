package com.asep.pelaporan_imaje.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.asep.pelaporan_imaje.config.BitmapFormat.bitmap_size;

public class PergantianPart extends AppCompatActivity {
    DatePickerDialog.OnDateSetListener onDateSetListener;
    Spinner sp_pelanggan,sp_sn;
    EditText et_tanggal,et_namaPart,et_pn,et_ket;
    ImageView iv_gambar,iv_foto,iv_galeri;
    Button bt_simpan;
    String[] namabulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    String[] pengerjaan = {"Laporan Kerusakan","Kunjungan Rutin","Pergantian Sparepart","Preventive Maintenance"};
    List<String> array_mp_id  ;
    List<String> array_mp_nama;
    List<String> array_mm_id;
    List<String> array_mm_sn  ;
    String tanggal;
    private static final int PICK_IMAGE_REQUEST=1, PICK_CAMERA_REQUEST=2;
    Bitmap bitmap,decoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pergantian_part);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_pergantianPart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        et_tanggal  =(EditText)findViewById(R.id.et_tanggal_pergantianPart);
        sp_pelanggan=(Spinner)findViewById(R.id.sp_pelanggan_pergantianPart);
        sp_sn       =(Spinner)findViewById(R.id.sp_sn_pergantianPart);
        et_pn       =(EditText)findViewById(R.id.et_partnumber_pergantianPart);
        et_namaPart =(EditText)findViewById(R.id.et_namasparepart_pergantianPart);
        et_ket      =(EditText)findViewById(R.id.et_keterangan_pergantianPart);
        iv_gambar   =(ImageView)findViewById(R.id.iv_gambar_pergantianPart);
        iv_foto     =(ImageView)findViewById(R.id.iv_foto_pergantianPart);
        iv_galeri   =(ImageView)findViewById(R.id.iv_galeri_pergantianPart);
        bt_simpan   =(Button)findViewById(R.id.bt_simpan_pergantianPart);

        dataSpinnerPelanggan();
        sp_pelanggan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataSpinnerSN(sp_pelanggan.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        et_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year    = calendar.get(Calendar.YEAR);
                int month   = calendar.get(Calendar.MONTH);
                int day     = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(PergantianPart.this,onDateSetListener,
                        year,month,day);
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = day+" "+namabulan[month]+" "+year;
                tanggal = year+"-"+month+"-"+day;
                et_tanggal.setText(date);
            }
        };
        iv_galeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        iv_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,PICK_CAMERA_REQUEST);
            }
        });
        bt_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpan();
            }
        });
    }

    private void dataSpinnerPelanggan(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.URL + "data_mesin/select_namaPT.php",new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("response :",response.toString());
                array_mp_id = new ArrayList<>();
                array_mp_nama = new ArrayList<>();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        array_mp_id.add(jsonObject.getString("mp_id"));
                        array_mp_nama.add(jsonObject.getString("mp_nama"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PergantianPart.this,android.R.layout.simple_spinner_item ,array_mp_nama );
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                sp_pelanggan.setAdapter(arrayAdapter);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    private void dataSpinnerSN(String namaPT){
        String url=Server.URL + "data_mesin/select_datamesin_by_namaPT.php?nama_pt="+namaPT;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url.replace(" ","%20"),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response :",response.toString());
                array_mm_id = new ArrayList<>();
                array_mm_sn = new ArrayList<>();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        array_mm_id.add(jsonObject.getString("mm_id"));
                        array_mm_sn.add(jsonObject.getString("mm_sn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(PergantianPart.this,android.R.layout.simple_spinner_item ,array_mm_sn );
                langAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                sp_sn.setAdapter(langAdapter);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void simpan(){
        final ProgressDialog loading = ProgressDialog.show(this, "Menyimpan", "Mohon Menungggu...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.URL + "sparepart/simpan_pergantian_part.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PergantianPart.this,Home.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Laporan gagal dikirim!", Toast.LENGTH_LONG).show();
                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("Error Response :",error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hs_tgl",tanggal);
                params.put("hs_pn", et_pn.getText().toString());
                params.put("hs_nama", et_namaPart.getText().toString());
                params.put("hs_ket", et_ket.getText().toString());
                params.put("hs_id_mesin", array_mm_id.get(sp_sn.getSelectedItemPosition()));
                params.put("hs_gambar", BitmapFormat.getStringImage(BitmapFormat.getBitmapFromImageView(iv_gambar)));
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
        }else if (requestCode == PICK_CAMERA_REQUEST ) {
            Bitmap bitmapCamera = (Bitmap)data.getExtras().get("data");
            iv_gambar.setImageBitmap(bitmapCamera);
        }

    }
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        iv_gambar.setImageBitmap(decoded);
    }
    // fungsi resize image
    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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

}
