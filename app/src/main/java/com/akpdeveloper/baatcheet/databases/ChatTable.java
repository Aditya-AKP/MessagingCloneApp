package com.akpdeveloper.baatcheet.databases;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.akpdeveloper.baatcheet.enums.MessageStatus;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.MessageModel;

import java.util.List;

@Entity(tableName = "chatTable")
public class ChatTable {
    @PrimaryKey
    @NonNull
    String chatID;
    String user1;
    String user2;
    @TypeConverters(Converters.class)
    MessageModel lastMessage;

    int newMessageNumber;

    public ChatTable(){}

    @Ignore
    public ChatTable(@NonNull String chatID, String user1, String user2, MessageModel lastMessage, int newMessageNumber) {
        this.chatID = chatID;
        this.user1 = user1;
        this.user2 = user2;
        this.lastMessage = lastMessage;
        this.newMessageNumber=newMessageNumber;
    }


    public int getNewMessageNumber() {
        return newMessageNumber;
    }

    public void setNewMessageNumber(int newMessageNumber) {
        this.newMessageNumber = newMessageNumber;
    }

    @NonNull
    public String getChatID() {
        return chatID;
    }

    public void setChatID(@NonNull String chatID) {
        this.chatID = chatID;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }


    public MessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }
}
