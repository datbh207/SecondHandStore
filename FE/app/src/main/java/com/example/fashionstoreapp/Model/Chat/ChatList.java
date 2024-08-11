package com.example.fashionstoreapp.Model.Chat;

public class ChatList {
    private String phone, name, message, date, time, sender;

    public ChatList(String name, String phone, String message, String date, String time, String sender) {
        this.name = name;
        this.phone = phone;
        this.message = message;
        this.date = date;
        this.time = time;
        this.sender = sender;
    }

    // Getters and setters for all fields, including sender
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
}