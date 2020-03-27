package com.asep.pelaporan_imaje.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.asep.pelaporan_imaje.R;
import com.daimajia.slider.library.SliderLayout;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class Home extends AppCompatActivity {
    private SliderLayout sliderLayout;
    SharedPreferences sharedPreferences;
    PhotoViewAttacher photoViewAttacher;
    TextView nama,pt;
    ImageView foto_profil;
    HashMap<String, String> data_slide ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tollbarHome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        nama = (TextView) findViewById(R.id.home_namaUsr);
        pt   = (TextView) findViewById(R.id.home_namaPT);
        foto_profil = (ImageView) findViewById(R.id.home_logoUsr);
        photoViewAttacher = new PhotoViewAttacher(foto_profil);
        photoViewAttacher.setZoomable(false);
        Picasso .get()
                .load("https://scontent.fcgk24-2.fna.fbcdn.net/v/t1.0-9/s960x960/74319322_2332455163549637_1926326161202216960_o.jpg?_nc_cat=103&_nc_ohc=jr7FzI-0tRkAX_3qWq_&_nc_ht=scontent.fcgk24-2.fna&_nc_tp=7&oh=b24b7e1a545b7cf0da865816e20e1b86&oe=5EF22271")
                .error(R.drawable.user_icon)
                .centerCrop()
                .fit()
                .transform(new com.asep.pelaporan_imaje.Picasso())
                .into(foto_profil);

        sharedPreferences = getSharedPreferences("shared_preference_users", Context.MODE_PRIVATE);
        String a = sharedPreferences.getString("nama_usr","");
        String b = sharedPreferences.getString("nama_pt","");

        nama.setText(a);
        pt.setText(b.toUpperCase());

//        code image slider
        ImageSlider imageSlider=findViewById(R.id.slider);

        List<SlideModel> slideModels=new ArrayList<>();
        slideModels.add(new SlideModel("http://192.168.43.103/pelaporan_imaje/images/slider/slide_gambar1.png"));
        slideModels.add(new SlideModel("http://192.168.43.103/pelaporan_imaje/images/slider/slide_gambar2.png"));
        slideModels.add(new SlideModel("http://192.168.43.103/pelaporan_imaje/images/slider/slide_gambar3.png"));
        slideModels.add(new SlideModel("http://192.168.43.103/pelaporan_imaje/images/slider/slide_gambar4.png"));
        imageSlider.setImageList(slideModels,true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.option,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
//            case R.id.action_infoAplikasi :
//                Intent intent = new Intent(Home.this,AboutApk.class);
//                startActivity(intent);
//                return true;
////            case R.id.action_login :
////                Intent i =new Intent(Home.this,Login.class);
////                finish();
////                startActivity(i);
////                return true;
//            case R.id.action_logout :
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.putBoolean(Login.session_status, false);
//                editor.putString(TAG_ID, null);
//                editor.putString(TAG_USERNAME, null);
//                editor.putString(TAG_STATUS, null);
//                editor.commit();
//                deleteToken(idToken);
//                Intent a =new Intent(Home.this,Login.class);
//                finish();
//                startActivity(a);
//
//                return true;

            default: return super.onOptionsItemSelected(item);

        }
    }

//    @Override
//    protected void onStop(){
//        sliderLayout.stopAutoCycle();
//        super.onStop();
//    }
//
//    @Override
//    public void onSliderClick(BaseSliderView slider) {
//        Toast.makeText(this,slider.getBundle().get("extra")+"",Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        Log.d("Slider","Page Change :"+position);
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }

}