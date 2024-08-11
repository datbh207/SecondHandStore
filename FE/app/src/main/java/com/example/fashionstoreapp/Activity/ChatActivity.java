package com.example.fashionstoreapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionstoreapp.Adapter.ChatAdapter;
import com.example.fashionstoreapp.Model.Chat.ChatList;
import com.example.fashionstoreapp.Model.Chat.MemoryData;
import com.example.fashionstoreapp.Model.User;
import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Somethings.ObjectSharedPreferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fashionstoreapp-6993d-default-rtdb.firebaseio.com/");
    private final List<ChatList> chatList = new ArrayList<>();
    private String getUserPhone = "";
    private String chatKey = "";
    private RecyclerView chatRecycleView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        User user = ObjectSharedPreferences.getSavedObjectFromPreference(ChatActivity.this, "User", "MODE_PRIVATE", User.class);
        System.out.println("USERRRR1: " + user);

        final ImageView btnBack = findViewById(R.id.btnBack);
        final TextView txtName = findViewById(R.id.txtName);
        final EditText editTextMessage = findViewById(R.id.editTextMessage);
        final ImageView btnSend = findViewById(R.id.btnSend);
        chatRecycleView = findViewById(R.id.chatRecycleView);

        final String getFromUser = getIntent().getStringExtra("name");  //from user
        chatKey = getIntent().getStringExtra("chat_key");
        final String getToUser = getIntent().getStringExtra("phone");  //to user

        if (chatKey == null) {
            chatKey = "1"; // Default chat key if null
        }

        getUserPhone = MemoryData.getData(ChatActivity.this);

        txtName.setText(getToUser);
        Log.d("ChatActivity", "getFromUser: " + getFromUser + ", chatKey: " + chatKey + ", getToUser: " + getToUser + ", getUserPhone: " + getUserPhone + ", user: " + user);


        chatRecycleView.setHasFixedSize(true);
        chatRecycleView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        chatAdapter = new ChatAdapter(chatList, ChatActivity.this, user);
        chatRecycleView.setAdapter(chatAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (chatKey.isEmpty() || chatKey == null) {
                    chatKey = "1";                        //generate chat key (default = 1)
                    if (snapshot.hasChild("chat")) {
                        chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                    }
                }


                if (snapshot.hasChild("chat")) {
                    chatList.clear();

                    for (DataSnapshot messageSnapshot : snapshot.child("chat").child(chatKey).child("message").getChildren()) {
                        if (messageSnapshot.hasChild("msg") && messageSnapshot.hasChild("sender")) {
                            final String messageTimestamp = messageSnapshot.getKey();
                            final String getMsg = messageSnapshot.child("msg").getValue(String.class);
                            final String sender = messageSnapshot.child("sender").getValue(String.class); // Get sender

                            long timestamp1 = Long.parseLong(messageTimestamp);
                            Date date = new Date(timestamp1 * 1000L);  //convert to milliseconds

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                            String formattedDate = simpleDateFormat.format(date);
                            String formattedTime = simpleTimeFormat.format(date);

                            ChatList list = new ChatList(getToUser, getFromUser, getMsg, formattedDate, formattedTime, sender); // Pass sender
                            chatList.add(list);

                            if (loadingFirstTime || Long.parseLong(messageTimestamp) > Long.parseLong(MemoryData.getLastMessage(ChatActivity.this, chatKey))) {
                                loadingFirstTime = false;
                                MemoryData.saveLastMessage(messageTimestamp, chatKey, ChatActivity.this);
                                chatAdapter.updateChatList(chatList);
                                chatRecycleView.scrollToPosition(chatList.size() - 1);
                            }
                        } else {
                            Log.e("FirebaseData", "Invalid message format: " + messageSnapshot.getValue());
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Update the database reference in ChatActivity.java to include the sender field

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String getTxtMessage = editTextMessage.getText().toString();
                String user1 = getToUser;  // to
                String user2 = user.getId();  // from

                if(user.getRole().equals("shipper")) {
                    chatKey = user1 + user2; // (to+from)
                } else {
                    chatKey = user2 + user1; // (to+from)
                }

                //get current timestamps
                final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);

                if (chatKey == null || chatKey.isEmpty()) {
                    chatKey = "1"; // Set default chat key if not initialized
                }

                databaseReference.child("chat").child(chatKey).child("user_1").setValue(user1);
                databaseReference.child("chat").child(chatKey).child("user_2").setValue(user2);
                databaseReference.child("chat").child(chatKey).child("message").child(currentTimestamp).child("msg").setValue(getTxtMessage);
                databaseReference.child("chat").child(chatKey).child("message").child(currentTimestamp).child("sender").setValue(user2); // Add sender field

                editTextMessage.setText("");  //clear
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}