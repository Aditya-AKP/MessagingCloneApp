package com.akpdeveloper.baatcheet;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.akpdeveloper.baatcheet.fragments.SettingsPreference;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheAppTheme(this);
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

    public static void setTheAppTheme(Context context){
        switch(PreferenceManager.getDefaultSharedPreferences(context).getString("preference_theme","system")){
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    public static void setTheLanguage(Context context){
        String languageToLoad  = "hi"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());

        config.setLocale(locale);
        context.createConfigurationContext(config);

    }
}