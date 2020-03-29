package com.asep.pelaporan_imaje.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
