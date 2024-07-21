package com.akpdeveloper.baatcheet.databases;

import static com.akpdeveloper.baatcheet.SplashActivity.logcat;

import androidx.room.TypeConverter;

import com.akpdeveloper.baatcheet.enums.MessageStatus;
import com.akpdeveloper.baatcheet.enums.MessageType;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.MessageModel;

import java.util.List;

public class Converters {

    @TypeConverter
    public String fromLastMessage(MessageModel messageModel){
        return messageModel.getMessageID() +
                "!_!&&!_!" +
                messageModel.getMessage() +
                "!_!&&!_!" +
                messageModel.getSenderID() +
                "!_!&&!_!" +
                messageModel.getType() +
                "!_!&&!_!" +
                messageModel.getMessageStatus() +
                "!_!&&!_!" +
                messageModel.getTimestamp().getSeconds() +
                "!_!&&!_!" +
                messageModel.getTimestamp().getNanoseconds();
    }

    @TypeConverter
    public MessageModel toLastMessage(String m){
        logcat("enter"+m);
        String[] mess = m.split("!_!&&!_!");
        for(int i=0;i<mess.length;i++){
            logcat(i+mess[i]+"\n");
        }
        return new MessageModel(
                mess[0],
                mess[1],
                mess[2],
                MessageType.valueOf(mess[3]),
                MessageStatus.valueOf(mess[4]),
                new DateModel(Long.parseLong(mess[5]),Integer.parseInt(mess[6]))
        );
    }
}
