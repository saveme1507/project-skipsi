package com.asep.pelaporan_imaje.config;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class BitmapFormat {
    public static Bitmap getBitmapFromImageView(ImageView view){
        Bitmap bitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
        return bitmap;
    }
}
