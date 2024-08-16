package com.akpdeveloper.baatcheet.databases;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messageTable")
public class MessageTable {
    @PrimaryKey
    @NonNull
    String ID;
    String friendUID;
    String message;
    String link;
    boolean sendByMe;
    int type;
    int status;

    Long timestamp;

    public MessageTable(@NonNull String ID, String friendUID, String message, String link, boolean sendByMe, int type, int status, Long timestamp) {
        this.ID=ID;
        this.friendUID = friendUID;
        this.message = message;
        this.link = link;
        this.sendByMe = sendByMe;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getID() {
        return ID;
    }

    public void setID(@NonNull String ID) {
        this.ID = ID;
    }

    public String getFriendUID() {
        return friendUID;
    }

    public void setFriendUID(String friendUID) {
        this.friendUID = friendUID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSendByMe() {
        return sendByMe;
    }

    public void setSendByMe(boolean sendByMe) {
        this.sendByMe = sendByMe;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
