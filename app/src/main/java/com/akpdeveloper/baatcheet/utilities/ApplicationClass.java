package com.akpdeveloper.baatcheet.utilities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class ApplicationClass extends Application {

    public static final String CHANNEL_ADMIN = "channel1";
    public static final String CHANNEL_CHAT = "channel2";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        NotificationChannel chatChannel = new NotificationChannel(CHANNEL_CHAT,"Chat Notification", NotificationManager.IMPORTANCE_HIGH);
        NotificationChannel adminChannel = new NotificationChannel(CHANNEL_ADMIN,"System Notification", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(chatChannel);
        notificationManager.createNotificationChannel(adminChannel);
    }
}
