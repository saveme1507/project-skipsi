package com.asep.pelaporan_imaje.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.server.Server;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class DetailSparepart extends AppCompatActivity implements DownloadFile.Listener {
    PDFPagerAdapter adapter;
    RemotePDFViewPager remotePDFViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sparepart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_master_sparepart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

         remotePDFViewPager = new RemotePDFViewPager(getApplicationContext(),
                        Server.URL+"master_katalog_sparepart/BAB_I.pdf",
                        this);
//        WebView webView = (WebView)findViewById(R.id.webview_spaprepart);
//        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progresbar_sparepart);
//
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
    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }
}
