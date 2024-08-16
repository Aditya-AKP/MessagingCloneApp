package com.akpdeveloper.baatcheet.services;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;
import static com.akpdeveloper.baatcheet.utilities.ApplicationClass.CHANNEL_CHAT;

import android.app.Notification;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    public static final String FIREBASE_USER_ID = "firebase_user_id";
    public static final String FIREBASE_TITLE = "firebase_title";
    public static final String FIREBASE_BODY = "firebase_body";


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Map<String,Object> updateToken = new HashMap<>();
        updateToken.put("token",token);
        try {
            FireBaseClass.allUsersCollectionReference().document(FireBaseClass.myUserUID()).update(updateToken);
        }catch (NullPointerException n){
            logcat("Token null generated: "+n.getMessage());
        }catch (Exception e){
            logcat("Token Exception: "+e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        logcat("onMessageReceived: "+message.getData());

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_CHAT)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(message.getData().get(FIREBASE_TITLE))
                .setContentText(message.getData().get(FIREBASE_BODY))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(12,notification);
    }
}
