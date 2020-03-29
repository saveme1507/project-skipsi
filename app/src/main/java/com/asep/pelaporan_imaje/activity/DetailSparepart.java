package com.asep.pelaporan_imaje.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asep.pelaporan_imaje.R;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class DetailSparepart extends AppCompatActivity implements DownloadFile.Listener {
    PDFPagerAdapter adapter;
    RemotePDFViewPager remotePDFViewPager;
    String nama,patch;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sparepart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_detail_sparepart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        progressBar = (ProgressBar)findViewById(R.id.progressbar_detail_sparepart);
        textView    = (TextView)findViewById(R.id.tx_judul_open_sparepart);

        nama   = getIntent().getStringExtra("nama");
        patch  = getIntent().getStringExtra("patch");

        textView.setText(nama.replace("-"," ").replace(".pdf",""));

        remotePDFViewPager = new RemotePDFViewPager(getApplicationContext(),
                        patch+nama,
                        this);

//        progressBar.setVisibility(View.VISIBLE);//dispaly the progres bar
//        String url = Server.URL+"master_katalog_sparepart/9040.pdf";
//        String finalUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url="+url;
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setBuiltInZoomControls(true);
//        webView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//                if(newProgress==50){
//                    progressBar.setVisibility(View.GONE);
//
//                }
//            }
//        });
//        webView.loadUrl(finalUrl);

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
