package com.akpdeveloper.baatcheet.models;

import android.net.Uri;

import com.akpdeveloper.baatcheet.enums.MessageStatus;
import com.akpdeveloper.baatcheet.enums.MessageType;
import com.google.firebase.Timestamp;

public class MessageModel {

    private String messageID;
    private String message;
    private String link;
    private String senderID;
    private MessageType type;
    private DateModel timestamp;
    private MessageStatus messageStatus;
    private String mimeType;

    public MessageModel() {
    }

    public MessageModel(String messageID,String message,String link, String senderID, MessageType type,MessageStatus messageStatus, DateModel timestamp,String mimeType) {
        this.messageID=messageID;
        this.message = message;
        this.link = link;
        this.senderID = senderID;
        this.type = type;
        this.timestamp = timestamp;
        this.messageStatus = messageStatus;
        this.mimeType = mimeType;
    }

    public String display(){
        return "Message ID: "+getMessageID()+"\n"+
                "Message Sender ID: "+getSenderID()+"\n"+
                "Message: "+getMessage()+"\n"+
                "Message Type: "+getType()+"\n"+
                "Message Link: "+getLink()+"\n"+
                "Message MIME Type: "+getMimeType()+"\n"+
                "Message Status: "+getMessageStatus()+"\n"+
                "Message Timestamp: "+getTimestamp()+"\n";
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
