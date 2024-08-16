package com.akpdeveloper.baatcheet.utilities;

import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.myUserUID;

import android.content.Context;
import android.content.Intent;

import androidx.room.Room;

import com.akpdeveloper.baatcheet.databases.AppDatabase;
import com.akpdeveloper.baatcheet.enums.MessageStatus;
import com.akpdeveloper.baatcheet.models.UserModel;

import java.util.List;
import java.util.Random;

public class AndroidUtils {

    public static AppDatabase DB ;
    public static UserModel MYSELF_USER;


    public static void setUserModelToIntent(Intent intent, UserModel userModel){
        intent.putExtra("username",userModel.getName());
        intent.putExtra("phone",userModel.getNumber());
        intent.putExtra("uid",userModel.getuID());
        intent.putExtra("image",userModel.getImageUrl());
        intent.putExtra("about",userModel.getAbout());
        intent.putExtra("token",userModel.getToken());

    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel user = new UserModel();
        user.setAbout(intent.getStringExtra("about"));
        user.setImageUrl(intent.getStringExtra("image"));
        user.setuID(intent.getStringExtra("uid"));
        user.setNumber(intent.getStringExtra("phone"));
        user.setName(intent.getStringExtra("username"));
        user.setToken(intent.getStringExtra("token"));
        return user;
    }

    public static String getOtherUserID(List<String> user){
        if(user.get(0).equals(myUserUID())){
            return user.get(1);
        }else{
            return user.get(0);
        }
    }

    public static String getChatIDForChat(String friend,String me){
        if(friend.hashCode()<me.hashCode()){
            return friend+"_"+me;
        }else{
            return me+"_"+friend;
        }
    }

    public static boolean isPrimaryKeyExistInMessageTable(String id){
        int a = DB.MessageTableDao().numberOfPrimaryKeyExist(id);
        return a != 0;
    }

    public static boolean isMessageExistInMessageTable(String message){
        int a = DB.MessageTableDao().numberOfMessages(message);
        return a!=0;
    }

    public static MessageStatus messageStatusForMessageInMessageTable(String key){
        return MessageStatus.values()[DB.MessageTableDao().getTheMessageStatus(key)];
    }


    public static String randomUniqueID(){
        char[] charSet = {'a','b','c','d','e','f','g','h','j','i','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C',
                'D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9','_','-'};
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<64;i++){
            int a = random.nextInt(charSet.length);
            sb.append(charSet[a]);
        }
        return sb.toString();
    }
}
