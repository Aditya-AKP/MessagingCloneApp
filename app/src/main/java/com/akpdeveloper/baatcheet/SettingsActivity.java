package com.akpdeveloper.baatcheet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.akpdeveloper.baatcheet.fragments.SettingsPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        getSupportActionBar().setTitle("Settings");

        if(findViewById(R.id.SettingsActivityFrameLayout)!=null){
            if(savedInstanceState!=null){
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.SettingsActivityFrameLayout,new SettingsPreference()).commit();
        }
    }
}