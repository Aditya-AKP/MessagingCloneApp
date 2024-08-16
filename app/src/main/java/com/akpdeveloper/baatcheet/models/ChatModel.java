package com.akpdeveloper.baatcheet.models;

import com.akpdeveloper.baatcheet.enums.MessageStatus;
import com.akpdeveloper.baatcheet.utilities.DateUtils;
import com.google.firebase.Timestamp;

import java.util.Comparator;
import java.util.List;

public class ChatModel {
    private String chatID;
    private List<String> userIDs;
    private MessageModel lastMessage;

    private Integer newMessageNumber;


    public ChatModel() {
    }

    public ChatModel(String chatID, List<String> userIDs, MessageModel lastMessage) {
        this.chatID = chatID;
        this.userIDs = userIDs;
        this.lastMessage = lastMessage;
        this.newMessageNumber = 0;
    }

    public ChatModel(String chatID, List<String> userIDs, MessageModel lastMessage,Integer newMessageNumber) {
        this.chatID = chatID;
        this.userIDs = userIDs;
        this.lastMessage = lastMessage;
        this.newMessageNumber = newMessageNumber;
    }

    public Integer getNewMessageNumber() {
        return newMessageNumber;
    }

    public void setNewMessageNumber(Integer newMessageNumber) {
        this.newMessageNumber = newMessageNumber;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<String> userIDs) {
        this.userIDs = userIDs;
    }

    public MessageModel getLastMessage() {
        return lastMessage!=null?lastMessage:new MessageModel();
    }

    public void setLastMessage(MessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }



}

