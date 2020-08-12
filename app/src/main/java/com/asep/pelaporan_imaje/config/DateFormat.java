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
    public static String currentDatetimeFormatSQL(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        String hasil = dateFormat.format(date);
        return hasil;
    }
    public static String dateTimeStatus(String dateSql){
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatOutput = new SimpleDateFormat("dd-MM-yyy HH:mm");
        String hasil = null;
        try {
            Date date = formatInput.parse(dateSql);
            hasil = formatOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hasil;
    }
    public static String dateTimeTanggal(String dateSql){
        int hari, bulan, tahun, jam, menit, detik;
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateHasil = null;
        try {
            Date dateInput = formatInput.parse(dateSql);
            GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance();
            date.setTime(dateInput);
            String namabulan[] = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
            hari = date.get(Calendar.DAY_OF_MONTH);
            bulan = date.get(Calendar.MONTH);
            tahun = date.get(Calendar.YEAR);
            jam     = date.get(Calendar.HOUR_OF_DAY);
            menit   = date.get(Calendar.MINUTE);
            dateHasil = String.valueOf(hari+" "+namabulan[bulan]+" "+tahun+"  "+jam+":"+menit);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return dateHasil;
    }
    public static String dateLabelChart(String dateSql){
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat formatOutput = new SimpleDateFormat("MMM yyyy");
        String hasil = null;
        try {
            Date date   = formatInput.parse(dateSql); //parse strign to date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date); //parse date to calender
            Date datehasil = calendar.getTime(); //parse calender to date
            hasil = formatOutput.format(datehasil);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hasil;
    }
    public static String currentDataReport() {
        int hari,tgl,bulan,tahun;
        String hasil = "";
        try {
            GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance();
            String namaHari[] = {"Sabtu","Minggu","Senin","Selasa","Rabu","Kamis","Jum'at"};
            String namabulan[] = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
            hari    = date.get(Calendar.DAY_OF_WEEK);
            tgl     = date.get(Calendar.DAY_OF_MONTH);
            bulan   = date.get(Calendar.MONTH);
            tahun   = date.get(Calendar.YEAR);
            hasil   = namaHari[hari] + " "+tgl+" "+namabulan[bulan]+ " " + tahun;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return hasil;
    }
}
