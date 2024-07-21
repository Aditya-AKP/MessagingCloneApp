package com.akpdeveloper.baatcheet.services;

import static com.akpdeveloper.baatcheet.SplashActivity.logcat;

import androidx.annotation.NonNull;

import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FCMService extends FirebaseMessagingService {
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
    }
}
