package com.asep.pelaporan_imaje.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.asep.pelaporan_imaje.R;
import com.asep.pelaporan_imaje.activity.fragment.FragmentLapMesin;
import com.asep.pelaporan_imaje.activity.fragment.FragmentPerPart;

import java.util.ArrayList;

public class Laporan extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        tabLayout   =(TabLayout)findViewById(R.id.tabLayout);
        viewPager   =(ViewPager)findViewById(R.id.viewPager);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.add(new FragmentLapMesin(),"Laporan Mesin");
        pagerAdapter.add(new FragmentPerPart(),"Pergantian Part");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0 : return new FragmentLapMesin();
                case 1 : return new FragmentPerPart();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void add(Fragment fragment, String str){
            fragments.add(fragment);
            strings.add(str);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings.get(position);
        }
    }
}


