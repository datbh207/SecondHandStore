package com.example.fashionstoreapp.Model.Chat;

public class Message {
    private String fromUser, toUser, lastMessage, chatKey;
    private int unseenMessage;

    public Message(String fromUser, String toUser,int unseenMessage, String chatKey) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.unseenMessage = unseenMessage;
        this.chatKey = chatKey;
    }


    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnseenMessage() {
        return unseenMessage;
    }

    public void setUnseenMessage(int unseenMessage) {
        this.unseenMessage = unseenMessage;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }
}
