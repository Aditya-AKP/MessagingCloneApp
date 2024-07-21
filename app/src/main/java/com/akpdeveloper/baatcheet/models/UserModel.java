package com.akpdeveloper.baatcheet.models;

public class UserModel {

    private String name;
    private String about;
    private String uID;
    private String number;
    private String imageUrl;
    private DateModel accountCreationTime;
    private String token;

    public UserModel() {}

    public UserModel(String name, String about, String uID,String number, String imageUrl, DateModel accountCreationTime) {
        this.token ="";
        this.name = name;
        this.about = about;
        this.uID = uID;
        this.number=number;
        this.imageUrl = imageUrl;
        this.accountCreationTime = accountCreationTime;
    }
    public UserModel(String name, String about, String uID,String number, String imageUrl, DateModel accountCreationTime,String token) {
        this.token = token;
        this.name = name;
        this.about = about;
        this.uID = uID;
        this.number=number;
        this.imageUrl = imageUrl;
        this.accountCreationTime = accountCreationTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DateModel getAccountCreationTime() {
        return accountCreationTime;
    }

    public void setAccountCreationTime(DateModel accountCreationTime) {
        this.accountCreationTime = accountCreationTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
