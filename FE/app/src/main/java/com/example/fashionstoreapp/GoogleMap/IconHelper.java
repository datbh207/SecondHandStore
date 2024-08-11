package com.example.fashionstoreapp.GoogleMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class IconHelper {
    public static BitmapDescriptor setIcon(Activity context, int drawableId){
        Drawable drawable = ActivityCompat.getDrawable(context, drawableId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth() / 10, drawable.getIntrinsicHeight() / 10);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() / 10, drawable.getIntrinsicHeight() / 10, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return  BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
