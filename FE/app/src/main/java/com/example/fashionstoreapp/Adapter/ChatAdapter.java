package com.example.fashionstoreapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionstoreapp.Activity.ChatActivity;
import com.example.fashionstoreapp.Model.Chat.ChatList;
import com.example.fashionstoreapp.Model.Chat.MemoryData;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatList> chatLists;
    private final Context context;
    private User user; // Add user variable

    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OPPO_MESSAGE = 2;

    public ChatAdapter(List<ChatList> chatLists, Context context, User user) {
        this.chatLists = chatLists;
        this.context = context;
        this.user = user; // Initialize userId
    }

    @Override
    public int getItemViewType(int position) {
        ChatList chat = chatLists.get(position);
        if (chat.getSender().equals(user.getId())) { // Ensure correct sender check
            return VIEW_TYPE_MY_MESSAGE;
        } else {
            return VIEW_TYPE_OPPO_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_my_message, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_oppo_message, parent, false);
            return new OppoMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatList chat = chatLists.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(chat);
        } else {
            ((OppoMessageViewHolder) holder).bind(chat);
        }
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public void updateChatList(List<ChatList> chatLists) {
        this.chatLists = chatLists;
        notifyDataSetChanged(); // Ensure list updates are reflected
    }

    class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView myMessage, myMessageTime, myMessageSender;

        MyMessageViewHolder(View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.myMessage);
            myMessageTime = itemView.findViewById(R.id.myMessageTime);
            myMessageSender = itemView.findViewById(R.id.myMessageSender); // Ensure sender view is referenced
        }

        void bind(ChatList chat) {
            myMessage.setText(chat.getMessage());
            myMessageTime.setText(chat.getTime());
            myMessageSender.setText(chat.getSender()); // Ensure sender is displayed
        }
    }

    class OppoMessageViewHolder extends RecyclerView.ViewHolder {
        TextView oppoMessage, oppoMessageTime, oppoMessageSender;

        OppoMessageViewHolder(View itemView) {
            super(itemView);
            oppoMessage = itemView.findViewById(R.id.oppoMessage);
            oppoMessageTime = itemView.findViewById(R.id.oppoMessageTime);
            oppoMessageSender = itemView.findViewById(R.id.oppoMessageSender); // Ensure sender view is referenced
        }

        void bind(ChatList chat) {
            oppoMessage.setText(chat.getMessage());
            oppoMessageTime.setText(chat.getTime());
            oppoMessageSender.setText(chat.getSender()); // Ensure sender is displayed
        }
    }
}
