package com.akpdeveloper.baatcheet.models;

import com.akpdeveloper.baatcheet.enums.StatusType;

import java.util.List;

public class StatusModel {

    String url;
    String text;
    DateModel date;
    String senderID;
    StatusType statusType;
    List<String> receiverIDs;

    public StatusModel() {}

    public StatusModel(String url, String text, DateModel date, String senderID, StatusType statusType,List<String> receiverIDs) {
        this.url = url;
        this.text = text;
        this.date = date;
        this.senderID = senderID;
        this.statusType = statusType;
        this.receiverIDs = receiverIDs;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public List<String> getReceiverIDs() {
        return receiverIDs;
    }

    public void setReceiverIDs(List<String> receiverIDs) {
        this.receiverIDs = receiverIDs;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DateModel getDate() {
        return date;
    }

    public void setDate(DateModel date) {
        this.date = date;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
