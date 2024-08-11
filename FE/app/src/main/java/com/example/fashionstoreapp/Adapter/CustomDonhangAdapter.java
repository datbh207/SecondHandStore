package com.example.fashionstoreapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.fashionstoreapp.Activity.ChatActivity;
import com.example.fashionstoreapp.GoogleMap.MapsActivity;
import com.example.fashionstoreapp.Model.Order;
import com.example.fashionstoreapp.Model.Order_Item;
import com.example.fashionstoreapp.R;
import com.google.android.gms.maps.GoogleMap;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CustomDonhangAdapter extends BaseAdapter {

    Context context;
    List<Order> orders;
    LayoutInflater inflater;

    public CustomDonhangAdapter(Context ctx, List<Order> orders) {
        this.context = ctx;
        this.orders = orders;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int i) {
        return orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_custom_list_view_don_hang, viewGroup, false);
            holder = new ViewHolder();
            holder.fullname = view.findViewById(R.id.fullnameOrder);
            holder.name = view.findViewById(R.id.nameItems);
            holder.date = view.findViewById(R.id.dateOrder);
            holder.price = view.findViewById(R.id.priceOrder);
            holder.quantity = view.findViewById(R.id.quantityOrder);
            holder.address = view.findViewById(R.id.addressOrder);
            holder.btnChat = view.findViewById(R.id.btnChat);
            holder.btnMap = view.findViewById(R.id.btnMap);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Order order = orders.get(i);

        // Set data to views
        holder.fullname.setText(order.getFullname());
        holder.name.setText(formatOrderItems(order));
        holder.date.setText(formatDate(order.getBooking_Date().toString()));
        holder.price.setText(formatPrice(order.getTotal()));
        holder.quantity.setText(String.valueOf(order.getOrder_Item().size()));
        holder.address.setText(order.getAddress());

        // Set OnClickListener for btnChat
        holder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", order.getNote());  //from user
                intent.putExtra("phone",order.getUser().getId()); // to user
                intent.putExtra("chat_key", order.getUser().getId()+order.getNote());

                context.startActivity(intent);
            }
        });
        holder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", order.getAddress());
                context.startActivity(intent);
            }
        });
        return view;
    }

    // Helper method to format order items
    private String formatOrderItems(Order order) {
        List<Order_Item> items = order.getOrder_Item();
        StringBuilder itemNames = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (i < 2) {
                itemNames.append(items.get(i).getProduct().getProduct_Name());
                if (i < items.size() - 1) {
                    itemNames.append(", ");
                }
            } else {
                itemNames.append("...");
                break;
            }
        }

        return itemNames.toString();
    }
    // Helper method to format date to dd-MM-yyyy
    private String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }

    // Helper method to format price with commas
    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
        return formatter.format(price);
    }
    static class ViewHolder {
        TextView fullname;
        TextView name;
        TextView date;
        TextView price;
        TextView quantity;
        TextView address;
        Button btnChat;
        Button btnMap;
    }
}
