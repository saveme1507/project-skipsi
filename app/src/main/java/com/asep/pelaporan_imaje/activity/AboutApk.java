package com.asep.pelaporan_imaje.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.asep.pelaporan_imaje.R;

public class AboutApk extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_apk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
        finish();
    }
}
