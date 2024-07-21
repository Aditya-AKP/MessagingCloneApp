package com.akpdeveloper.baatcheet.databases;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

@Entity(tableName = "contactTable")
public class ContactTable {

    String customName;
    String name;
    String number;
    String about;
    @PrimaryKey
    @NonNull
    String UID;

    String imageUrl;
    Long accountCreationTime;

    public ContactTable(String name, String number, String about, @NonNull String UID, String imageUrl, Long accountCreationTime) {
        this.name = name;
        this.number = number;
        this.about = about;
        this.UID = UID;
        this.imageUrl = imageUrl;
        this.accountCreationTime = accountCreationTime;
        this.customName="";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @NonNull
    public String getUID() {
        return UID;
    }

    public void setUID(@NonNull String UID) {
        this.UID = UID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getAccountCreationTime() {
        return accountCreationTime;
    }

    public void setAccountCreationTime(Long accountCreationTime) {
        this.accountCreationTime = accountCreationTime;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
