package com.asep.pelaporan_imaje.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.asep.pelaporan_imaje.R;

public class DetailHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detailhome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }
}
