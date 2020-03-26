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
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import uk.co.senab.photoview.PhotoViewAttacher;

public class Home extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    SliderLayout sliderLayout;
    SharedPreferences sharedPreferences;
    PhotoViewAttacher photoViewAttacher;
    TextView nama,pt;
    ImageView foto_profil;

    HashMap<String,String> data_img_slide = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tollbarHome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        nama = (TextView) findViewById(R.id.home_namaUsr);
        pt   = (TextView) findViewById(R.id.home_namaPT);
        sliderLayout = (SliderLayout) findViewById(R.id.home_slider);
        foto_profil = (ImageView) findViewById(R.id.home_logoUsr);
        photoViewAttacher = new PhotoViewAttacher(foto_profil);
        photoViewAttacher.setZoomable(false);
        Picasso
                .get()
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

    private void image_slide(){
        data_img_slide.put("slide1","http://localhost/pelaporan_imaje/images/slider/slide_1.png");
        data_img_slide.put("slide2", "http://localhost/pelaporan_imaje/images/slider/slide_2.png");
        data_img_slide.put("slide3", "http://localhost/pelaporan_imaje/images/slider/slide_3.png");
        data_img_slide.put("slide4", "http://localhost/pelaporan_imaje/images/slider/slide_4.png");

        for(String name : data_img_slide.keySet()){
            TextSliderView textSliderView = new TextSliderView(Home.this);
            textSliderView
                    .description(name)
                    .image(data_img_slide.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.FlipHorizontal);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        sliderLayout.addOnPageChangeListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
