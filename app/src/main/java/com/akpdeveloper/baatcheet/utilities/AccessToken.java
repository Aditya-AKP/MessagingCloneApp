package com.akpdeveloper.baatcheet.utilities;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;

import com.google.api.client.util.Lists;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public class AccessToken {

    private static  final  String firebaseMessagingScope = "";

    public String getAccessToken(){
        try{
            String jsonString = "";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(Collections.singletonList(firebaseMessagingScope));

            googleCredentials.refresh();

            return googleCredentials.getAccessToken().getTokenValue();

        }catch (IOException e){
            logcat("Access Token error message: "+e.getMessage());
            return null;
        }
    }
}
