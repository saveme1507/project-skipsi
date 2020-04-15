package com.asep.pelaporan_imaje.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.config.DateFormat;
import com.asep.pelaporan_imaje.server.Server;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PerformaMesin extends AppCompatActivity {
    ArrayList<BarEntry> barEntries;
    ArrayList<String> barLabels;
    BarDataSet barDataSet;
    BarData barData;
    BarChart chart1;
    TextView tx_sn,tx_tipe,tx_line,tx_pm;
    String sn,tipe,line,pm,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performa_mesin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar_performamesin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        chart1 = (BarChart) findViewById(R.id.chart_performamesin);
        tx_tipe =(TextView)findViewById(R.id.tx_mmTipe_perfom);
        tx_sn   =(TextView)findViewById(R.id.tx_mmSn_perfom);
        tx_line =(TextView)findViewById(R.id.tx_mmLine_perfom);
        tx_pm   =(TextView)findViewById(R.id.tx_mmLastPm_perfom);

        id  = getIntent().getStringExtra("mm_id");
        tipe = getIntent().getStringExtra("mm_tipe");
        sn  = getIntent().getStringExtra("mm_sn");
        line =getIntent().getStringExtra("mm_line");
        pm   =getIntent().getStringExtra("mm_lastPm");

        tx_tipe.setText(tipe);
        tx_sn.setText(sn);
        tx_pm.setText(DateFormat.dd_mmm_yyyy(pm));
        tx_line.setText(line);
        dataGrafik();


    }
    public class MyValueFormatter extends ValueFormatter implements IValueFormatter{
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler){
            return "";
        }
    }
    private void dataGrafik(){
        StringRequest strReq = new StringRequest(Request.Method.GET, Server.URL+"data_mesin/select_performa.php?mm_id="+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: " ,response.toString());
                try {
                    barEntries = new ArrayList<BarEntry>();
                    barLabels = new ArrayList<String>();
                    JSONObject jObj = new JSONObject(response);
                        barLabels.add("");
                        barEntries.add(new BarEntry(1, Float.parseFloat(jObj.getString("jml3"))));
                        barLabels.add(DateFormat.dateLabelChart(jObj.getString("bulan3")));
                        barEntries.add(new BarEntry(2, Float.parseFloat(jObj.getString("jml2"))));
                        barLabels.add(DateFormat.dateLabelChart(jObj.getString("bulan2")));
                        barEntries.add(new BarEntry(3, Float.parseFloat(jObj.getString("jml1"))));
                        barLabels.add(DateFormat.dateLabelChart(jObj.getString("bulan1")));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                barDataSet = new BarDataSet(barEntries, "Jumlah kerusakan perbulan");
                barDataSet.setValueFormatter(new MyValueFormatter());
                barData = new BarData(barDataSet);
                chart1.getXAxis().setValueFormatter(
                        new IndexAxisValueFormatter(barLabels));
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                chart1.setData(barData);
                chart1.animateY(3000);
                chart1.getDescription().setEnabled(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d( "Login Error: " , error.getMessage());
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
}


