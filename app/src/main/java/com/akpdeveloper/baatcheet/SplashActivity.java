package com.akpdeveloper.baatcheet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    public static void makeToast(Context con,String message,int length){Toast.makeText(con,message,length).show();}
    public static void makeToast(Context con,String message){Toast.makeText(con,message,Toast.LENGTH_SHORT).show();}
    public static void logcat(String message){Log.d("logcatme",message);}

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logcat("start onCreate SplashActivity");

        super.onCreate(savedInstanceState);

//        startActivity(new Intent(this,LoginActivity.class));

        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            startActivity(new Intent(this,LoginActivity.class));
        }
        finish();
        logcat("end onCreate SplashActivity");
    }
}