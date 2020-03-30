package com.asep.pelaporan_imaje.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateFormat {
    public static String dd_mmm_yyyy(String dateSql){
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatOutput = new SimpleDateFormat("dd MMM yyyy");
        String hasil = null;
        try {
            Date date = formatInput.parse(dateSql);
            hasil = formatOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hasil;
    }
    public static String dateNextPm(String dateSql){
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatOutput = new SimpleDateFormat("dd MMM yyyy");
        String hasil = null;
        try {
            Date date   = formatInput.parse(dateSql); //parse strign to date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date); //parse date to calender
            calendar.add(Calendar.MONTH,6); //penambahan bulan
            Date datehasil = calendar.getTime(); //parse calender to date
            hasil = formatOutput.format(datehasil);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hasil;
    }
    public static String dd_MMMM_yyyy(String dateSql) {
        int hari, bulan, tahun;
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd");
        String dateHasil = null;
        try {
            Date dateInput = formatInput.parse(dateSql);
            GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance();
            date.setTime(dateInput);
            String namabulan[] = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
            hari = date.get(Calendar.DAY_OF_MONTH);
            bulan = date.get(Calendar.MONTH);
            tahun = date.get(Calendar.YEAR);
            dateHasil = String.valueOf(hari + " " + namabulan[bulan]+ " " + tahun);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return dateHasil;
    }
}
