package com.asep.pelaporan_imaje.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.server.Server;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class Bantuan extends AppCompatActivity implements DownloadFile.Listener {
    PDFPagerAdapter adapter;
    RemotePDFViewPager remotePDFViewPager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_bantuan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        progressBar = (ProgressBar)findViewById(R.id.progressbar_detail_sparepart);


        remotePDFViewPager = new RemotePDFViewPager(getApplicationContext(),
                Server.URL+"bantuan/bantuan.pdf",
                this);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        progressBar.setVisibility(View.VISIBLE);
    }
}
