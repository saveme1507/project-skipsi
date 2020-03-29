package com.asep.pelaporan_imaje.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.server.Server;

public class Sparepart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sparepart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_master_sparepart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        WebView wv = (WebView)findViewById(R.id.web_view_datamesin);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAllowFileAccess(true);
        wv.loadUrl("https://docs.google.com/gview?embedded=true&url="+ Server.URL+"master_katalog_sparepart/9040.pdf");
    }
}
