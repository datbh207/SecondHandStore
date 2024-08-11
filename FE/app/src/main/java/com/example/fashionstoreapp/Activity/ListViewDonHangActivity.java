package com.example.fashionstoreapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.fashionstoreapp.Adapter.CustomDonhangAdapter;
import com.example.fashionstoreapp.Adapter.ProductAdapter;
import com.example.fashionstoreapp.Model.Order;
import com.example.fashionstoreapp.Model.Product;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.OrderAPI;
import com.example.fashionstoreapp.Retrofit.ProductAPI;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewDonHangActivity extends AppCompatActivity {
    CustomDonhangAdapter customDonhangAdapter;
    List<Order> orders = new ArrayList<>();
    ListView listView;

    protected void onCreate(Bundle saveInstanceBundle){
        super.onCreate(saveInstanceBundle);
        setContentView(R.layout.listview_don_hang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        listView = (ListView) findViewById(R.id.listviewDonhang);
        customDonhangAdapter = new CustomDonhangAdapter(this, orders);
        listView.setAdapter(customDonhangAdapter);
        loadOrder();

    }

    private void loadOrder() {
        User user = ObjectSharedPreferences.getSavedObjectFromPreference(ListViewDonHangActivity.this, "User", "MODE_PRIVATE", User.class);
        Log.d("Shipper ID", "Shipper ID: " + user.getId());
        OrderAPI.orderAPI.getOrderByShipper(user.getId()).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders.clear();
                    orders.addAll(response.body());
                    customDonhangAdapter.notifyDataSetChanged();
                    for (Order order : orders) {
                        Log.d("Orderr", order.toString());
                    }
                } else {
                    Log.e("API Error", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("====", "Call API Search fail", t);
            }
        });
    }
}
