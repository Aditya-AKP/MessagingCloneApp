package com.akpdeveloper.baatcheet.databases;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;

import android.net.Uri;

import androidx.room.TypeConverter;

import com.akpdeveloper.baatcheet.enums.MessageStatus;
import com.akpdeveloper.baatcheet.enums.MessageType;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.MessageModel;

public class Converters {

    @TypeConverter
    public String fromLastMessage(MessageModel messageModel){
        return messageModel.getMessageID() +
                "!_!&&!_!" +
                messageModel.getMessage() +
                "!_!&&!_!" +
                messageModel.getSenderID() +
                "!_!&&!_!" +
                messageModel.getLink() +
                "!_!&&!_!" +
                messageModel.getType() +
                "!_!&&!_!" +
                messageModel.getMessageStatus() +
                "!_!&&!_!" +
                messageModel.getTimestamp().getSeconds() +
                "!_!&&!_!" +
                messageModel.getTimestamp().getNanoseconds()+
                "!_!&&!_!"+
                messageModel.getMimeType();
    }

    @TypeConverter
    public MessageModel toLastMessage(String m){
        logcat("enter"+m);
        String[] mess = m.split("!_!&&!_!");
        for(int i=0;i<mess.length;i++){
            logcat(i+mess[i]+"\n");
        }
        if(mess.length==8){
            return new MessageModel(
                    mess[0],
                    mess[1],
                    mess[2],
                    mess[3],
                    MessageType.valueOf(mess[4]),
                    MessageStatus.valueOf(mess[5]),
                    new DateModel(Long.parseLong(mess[6]),Integer.parseInt(mess[7])),
                    null
            );
        }
        return new MessageModel(
                mess[0],
                mess[1],
                mess[2],
                mess[3],
                MessageType.valueOf(mess[4]),
                MessageStatus.valueOf(mess[5]),
                new DateModel(Long.parseLong(mess[6]),Integer.parseInt(mess[7])),
                mess[8]
        );
    }
}
