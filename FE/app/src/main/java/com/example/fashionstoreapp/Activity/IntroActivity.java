package com.example.fashionstoreapp.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.fashionstoreapp.Model.Cart;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.CartAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends AppCompatActivity {
    User isLoged;
    List<Cart> listCart = new ArrayList<>();
    TextView tvStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        sendNotificationIfCartOfUserNotNull();
        AnhXa();
        tvStartClick();
    }

    private void tvStartClick() {
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoged = ObjectSharedPreferences.getSavedObjectFromPreference(IntroActivity.this, "User", "MODE_PRIVATE", User.class);
//        Log.e("loged", isLoged.toString());
                if (isLoged != null){
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                }
            }
        });
    }

    private void AnhXa() {
        tvStart = findViewById(R.id.tvStart);
    }

    private void sendNotificationIfCartOfUserNotNull() {
        try {
            isLoged = ObjectSharedPreferences.getSavedObjectFromPreference(IntroActivity.this, "User", "MODE_PRIVATE", User.class);
            if (isLoged != null) {
                CartAPI.cartAPI.cartOfUser(isLoged.getId()).enqueue(new Callback<List<Cart>>() {
                    @Override
                    public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                        listCart = response.body();

                        if (listCart.size() != 0){
                            String productNames = new String("");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                for (Cart c : listCart) {
                                    productNames += c.getProduct().getProduct_Name() + "\n";
                                }
                            }
                            makeNotification(productNames); // notify user when cart of this user is not null
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Cart>> call, Throwable t) {
                        Log.e("====", "Call API Cart of user fail");
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void makeNotification(String productName) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground); // set the image of notification
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle(); // because setContentTitle() does not accept a String with multiple lines
        bigTextStyle.bigText(productName + "\n" + "Mau vào giỏ hàng thanh toán đi nào"); // so we use bigText() instead

        // Setup the notification
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setLargeIcon(bitmap);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setColor(Color.CYAN);
        builder.setSmallIcon(R.drawable.icon_cart_selected);
        builder.setContentTitle("Úi, giỏ hàng bạn còn đồ kìa!!!");
        builder.setStyle(bigTextStyle);
        builder.setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(channelID);
            if (channel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                channel = new NotificationChannel(channelID, "Some description", importance);
                channel.setLightColor(Color.GREEN);
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }
        }
        manager.notify(0, builder.build());
    }
}
