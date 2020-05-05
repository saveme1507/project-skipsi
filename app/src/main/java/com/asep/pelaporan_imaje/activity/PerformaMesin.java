package com.asep.pelaporan_imaje.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PerformaMesin extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    PerformaMesin.RecycleAdapterPerforma recycleAdapterPerforma;
    List<PerformaMesin.ItemPerforma> itemPerformas;
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
        recyclerView =(RecyclerView)findViewById(R.id.recycle_item_performa);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

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
        getDataHistoriPart(id);
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
    private void getDataHistoriPart(String mm_id){
        String url= Server.URL + "data_mesin/pergantian_part.php?mm_id="+mm_id;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url.replace(" ","%20"),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response :",response.toString());
                itemPerformas = new ArrayList<>();
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        PerformaMesin.ItemPerforma myiItemPerforma = new PerformaMesin.ItemPerforma(
                                jsonObject.getString("hs_id"),
                                jsonObject.getString("hs_tgl"),
                                jsonObject.getString("hs_nama")
                        );
                        itemPerformas.add(myiItemPerforma);
                        recycleAdapterPerforma  = new PerformaMesin.RecycleAdapterPerforma(itemPerformas,PerformaMesin.this);
                        recycleAdapterPerforma.notifyDataSetChanged();
                        recyclerView.setAdapter(recycleAdapterPerforma);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Response :",error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ItemPerforma{
        String hs_id,hs_tgl,hs_nama;

        public ItemPerforma(String hs_id, String hs_tgl, String hs_nama) {
            this.hs_id = hs_id;
            this.hs_tgl = hs_tgl;
            this.hs_nama = hs_nama;
        }

        public String getHs_id() {
            return hs_id;
        }

        public void setHs_id(String hs_id) {
            this.hs_id = hs_id;
        }

        public String getHs_tgl() {
            return hs_tgl;
        }

        public void setHs_tgl(String hs_tgl) {
            this.hs_tgl = hs_tgl;
        }

        public String getHs_nama() {
            return hs_nama;
        }

        public void setHs_nama(String hs_nama) {
            this.hs_nama = hs_nama;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    class RecycleAdapterPerforma extends RecyclerView.Adapter<PerformaMesin.RecycleAdapterPerforma.MyViewHolderPerforma>{
        List<PerformaMesin.ItemPerforma> itemPerformas;
        Context context;

        public RecycleAdapterPerforma(List<ItemPerforma> itemPerformas, Context context) {
            this.itemPerformas = itemPerformas;
            this.context = context;
        }

        @Override
        public PerformaMesin.RecycleAdapterPerforma.MyViewHolderPerforma onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_perfoma,viewGroup,false);
            return  new PerformaMesin.RecycleAdapterPerforma.MyViewHolderPerforma(view);
        }

        @Override
        public void onBindViewHolder(PerformaMesin.RecycleAdapterPerforma.MyViewHolderPerforma myViewHolderPerforma, int i) {
            myViewHolderPerforma.tx_nama.setText(itemPerformas.get(i).hs_nama);
            myViewHolderPerforma.tx_tgl.setText(itemPerformas.get(i).hs_tgl);
        }

        @Override
        public int getItemCount() {
            return itemPerformas.size();
        }

        public class MyViewHolderPerforma extends RecyclerView.ViewHolder{
            TextView tx_tgl,tx_nama;
            public MyViewHolderPerforma(View itemView) {
                super(itemView);
                tx_tgl =itemView.findViewById(R.id.tx_tgl_itemperforma);
                tx_nama =itemView.findViewById(R.id.tx_namapart_itemperforma);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PerformaMesin.this,DetailLaporan.class);
                        intent.putExtra("id_intent","per_part");
                        intent.putExtra("hlm_id",itemPerformas.get(getAdapterPosition()).hs_id);
                        intent.putExtra("flag","");
                        startActivity(intent);
                    }
                });
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

}


