package com.akpdeveloper.baatcheet.fragments;

import static com.akpdeveloper.baatcheet.SplashActivity.makeToast;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.MYSELF_USER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.fragments.settingsfragments.ProfilePicPreference;

public class SettingsPreference extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference,rootKey);

        ProfilePicPreference profilePic = findPreference("preference_profile_pic");

        if(profilePic!=null){
            profilePic.setProfilePicPreference(MYSELF_USER.getName(), MYSELF_USER.getAbout(), MYSELF_USER.getImageUrl(), view -> {
                //TODO : start the new activity for changing of name and image and about

            });
        }

    }
}
