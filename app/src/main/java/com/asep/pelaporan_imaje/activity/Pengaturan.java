package com.asep.pelaporan_imaje.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.server.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import in.anshul.libray.PasswordEditText;

import static com.asep.pelaporan_imaje.activity.Login.session_status;

public class Pengaturan extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    AlertDialog.Builder alertDialogBuilder, builderProfil;
    LayoutInflater layoutInflater, inflaterProfil;
    View view, viewProfil;
    String mu_id,mu_pass;
    PasswordEditText et_passOld,et_passNew,et_confirm;
    EditText et_user,et_telp;
    Switch aSwitch;
    ImageView iv_image;
    private static final int PICK_IMAGE_REQUEST =1 ;
        int bitmap_size = 60; // range 1 - 100
    Bitmap bitmap, decoded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_pengaturan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        Button bt_logout    = (Button)findViewById(R.id.btn_logout_pengaturan);
        TextView tx_pass    =(TextView)findViewById(R.id.tx_gantiPass_pengaturan);
        TextView tx_user    =(TextView)findViewById(R.id.tx_gantiUser_pengaturan);
        TextView tx_telp    =(TextView)findViewById(R.id.tx_gantiHp_pengaturan);
        TextView tx_hapusAkun   =(TextView)findViewById(R.id.tx_hapusAkun_pengaturan);
        TextView tx_profil  =(TextView)findViewById(R.id.tx_editProfil_pengaturan);
        aSwitch             = findViewById(R.id.switch_audio);

        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        mu_id   = sharedPreferences.getString("mu_id","");
        mu_pass = sharedPreferences.getString("mu_pass","");
        if (sharedPreferences.getBoolean("audioWellcome",false)){
            aSwitch.setChecked(true);
        }

        tx_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditprofil();
            }
        });
        tx_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEdituser();
            }
        });
        tx_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditpass();
            }
        });
        tx_telp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEdittelp();
            }
        });
        tx_hapusAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHapusAkun();
            }
        });
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("audioWellcome", true);
                if (aSwitch.isChecked()){
                    editor.putBoolean("audioWellcome", true);
                    editor.commit();
                }else{
                    editor.putBoolean("audioWellcome", false);
                    editor.commit();
                }

            }
        });
    }

    private void dialogEditprofil(){
        builderProfil = new AlertDialog.Builder(Pengaturan.this);
        inflaterProfil = getLayoutInflater();
        viewProfil = inflaterProfil.inflate(R.layout.dialog_edit_profil, null);
        builderProfil.setView(viewProfil);
        builderProfil.setCancelable(false);
        iv_image  =(ImageView)viewProfil.findViewById(R.id.iv_image_dialogEDitprofil);
        Button bt_pilihGmambar =(Button)viewProfil.findViewById(R.id.bt_pilihImage_dialogEditprofil);

        builderProfil.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (iv_image.getDrawable()!=null){
                    simpanProfil();
                }else {
                    Toast.makeText(viewProfil.getContext(),"Harap masukan foto atau gamabar",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builderProfil.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        bt_pilihGmambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        builderProfil.show();
    }
    private void dialogEditpass() {
        alertDialogBuilder = new AlertDialog.Builder(Pengaturan.this);
        layoutInflater = getLayoutInflater();
        view = layoutInflater.inflate(R.layout.dialog_edit_pass, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
//        alertDialogBuilder.setIcon();
//        alertDialogBuilder.setTitle("Edit Password");
        et_passOld = (PasswordEditText)view.findViewById(R.id.et_passOld_dialogPass);
        et_passNew = (PasswordEditText)view.findViewById(R.id.et_passNew_dialogPass);
        et_confirm = (PasswordEditText)view.findViewById(R.id.et_confirmPass_dialogPass);

        alertDialogBuilder.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String passOld     = et_passOld.getText().toString();
                String passnNew    = et_passNew.getText().toString();
                String confirm     = et_confirm.getText().toString();
                if(passOld.trim().length()>0 && passnNew.trim().length()>0 && confirm.trim().length()>0){
                    if(passnNew.equals(confirm)){
                        if (passOld.equals(mu_pass)){
                            editPass(mu_id,passnNew);
                        }else {
                            Toast.makeText(view.getContext(),"Password saat ini salah",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(view.getContext(),"confirm password tidak sama",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(view.getContext(),"Kolom tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
    private void dialogEdituser(){
        alertDialogBuilder = new AlertDialog.Builder(Pengaturan.this);
        layoutInflater = getLayoutInflater();
        view = layoutInflater.inflate(R.layout.dialog_edit_user, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);

        et_user = (EditText)view.findViewById(R.id.et_user_dialogUser);

        alertDialogBuilder.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String user = et_user.getText().toString();
                if (user.trim().length()>0){
                    editUser(mu_id,user);
                }else{
                    Toast.makeText(getApplicationContext(),"Kolom tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
    private void dialogEdittelp(){
        alertDialogBuilder = new AlertDialog.Builder(Pengaturan.this);
        layoutInflater = getLayoutInflater();
        view = layoutInflater.inflate(R.layout.dialog_edit_hp, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);

        et_telp =(EditText)view.findViewById(R.id.et_hp_dialogHp);

        alertDialogBuilder.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String telp =et_telp.getText().toString();
                if (telp.trim().length()>0){
                    if (telp.trim().length()==12){
                        editTelp(mu_id,telp);
                    }else{
                        Toast.makeText(view.getContext(),"Nomer HP harus 12 digit, tidak pakai spasi",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(view.getContext(),"Kolom tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
    private void dialogHapusAkun(){
        alertDialogBuilder = new AlertDialog.Builder(Pengaturan.this);
        alertDialogBuilder.setTitle("Konfirmasi");
        alertDialogBuilder.setMessage("Apakah anda yakin mengahapus akun ini?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hapusAkun(mu_id);
            }
        });
        alertDialogBuilder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void editPass(final String mu_id, final String passNew){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"pengaturan/update_password.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :",response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                    logOut();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Edit password gagal",Toast.LENGTH_SHORT).show();
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
                params.put("mu_id", mu_id);
                params.put("mu_pass", passNew);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void editUser(final String mu_id, final String user){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"pengaturan/update_username.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("mu_nama", user);
                        editor.commit();
                        Intent intent = new Intent(Pengaturan.this,Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Edit username gagal",Toast.LENGTH_SHORT).show();
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
                params.put("mu_id", mu_id);
                params.put("mu_nama", user);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void editTelp(final String mu_id, final String telp){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"pengaturan/update_telp.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :",response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success")==1){
                        Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("mu_telp", telp);
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Edit nomer handphone gagal",Toast.LENGTH_SHORT).show();
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
                params.put("mu_id", mu_id);
                params.put("mu_telp", telp.trim());
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void hapusAkun(final String mu_id){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"pengaturan/delete_akun.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response :",response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), jObj.getString("message"),Toast.LENGTH_SHORT).show();
                    logOut();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Hapus akun gagal",Toast.LENGTH_SHORT).show();
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
                params.put("mu_id", mu_id);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
    private void simpanProfil() {
        final ProgressDialog loading = ProgressDialog.show(this, "Menyimpan", "Mohon Menungggu...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Server.URL+"pengaturan/update_profil.php",new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ",response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getInt("success") == 1) {
                        Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("mu_logo", jObj.getString("patch"));
                        editor.commit();
                        Log.d("test",sharedPreferences.getString("mu_logo","jika gagal"));
                        Intent intent = new Intent(Pengaturan.this,Home.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Edit profil gagal", Toast.LENGTH_SHORT).show();
                }
                //menghilangkan progress dialog
                loading.dismiss();
            }},new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //menghilangkan progress dialog
                    loading.dismiss();
                    //menampilkan toast
                    Log.e("Error response :", error.getMessage());
                }
            }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mu_logo", getStringImage(decoded));
                params.put("mu_id", mu_id);
                params.put("hapus_file_lama",sharedPreferences.getString("mu_logo","").replace(
                        Server.URL+"pengaturan/images_profil/",
                        "C:\\xampp2\\htdocs\\pelaporan_imaje\\pengaturan\\images_profil\\"));
//                "/home/artisanc/public_html/app-pelaporan/pelaporan_imaje/pengaturan/images_profil/"
//                "C:\\xampp2\\htdocs\\pelaporan_imaje\\pengaturan\\images_profil\\"
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void logOut(){
        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("mu_id","");
        tambahToken(id);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(session_status, false);
        editor.putString("mu_id", null);
        editor.putString("mu_nama", null);
        editor.putString("mu_telp", null);
        editor.putString("mu_email", null);
        editor.putString("mu_pass", null);
        editor.putString("mu_logo", null);
        editor.putString("mu_token", null);
        editor.putString("mu_id_pt", null);
        editor.putString("mp_nama",null);
        editor.commit();
        Intent a =new Intent(Pengaturan.this,SpashScreen.class);
        startActivity(a);
        finishAffinity();
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
        iv_image.setImageBitmap(decoded);
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
    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void tambahToken(final String id){
        StringRequest strReq = new StringRequest(Request.Method.POST, Server.URL+"login/hapus_token.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d( "Hapus token : " , response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("hapus token Error: " , error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mu_id",id);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
}
