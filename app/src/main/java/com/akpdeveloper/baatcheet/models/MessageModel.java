package com.akpdeveloper.baatcheet.models;

import com.akpdeveloper.baatcheet.enums.MessageStatus;
import com.akpdeveloper.baatcheet.enums.MessageType;
import com.google.firebase.Timestamp;

public class MessageModel {

    private String messageID;
    private String message;
    private String senderID;
    private MessageType type;
    private DateModel timestamp;
    private MessageStatus messageStatus;

    public MessageModel() {
    }

    public MessageModel(String messageID,String message, String senderID, MessageType type,MessageStatus messageStatus, DateModel timestamp) {
        this.messageID=messageID;
        this.message = message;
        this.senderID = senderID;
        this.type = type;
        this.timestamp = timestamp;
        this.messageStatus = messageStatus;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public DateModel getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateModel timestamp) {
        this.timestamp = timestamp;
    }
    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

}
