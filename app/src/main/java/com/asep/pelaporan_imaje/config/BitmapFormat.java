package com.asep.pelaporan_imaje.config;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class BitmapFormat {
    public  static int bitmap_size = 60; //rannge 1-100

    public static Bitmap getBitmapFromImageView(ImageView view){
        Bitmap bitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
        return bitmap;
    }
    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
