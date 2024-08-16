package com.akpdeveloper.baatcheet.fragments;

import static androidx.core.app.ActivityCompat.finishAffinity;
import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.StartActivity.makeToast;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.MYSELF_USER;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.fragments.settingsfragments.ProfilePicFragment;
import com.akpdeveloper.baatcheet.fragments.settingsfragments.ProfilePicPreference;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;

import java.util.Objects;

public class SettingsPreference extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference,rootKey);

        ProfilePicPreference profilePic = findPreference("preference_profile_pic");

        if(profilePic!=null){
            profilePic.setProfilePicPreference(MYSELF_USER.getName(), MYSELF_USER.getAbout(), MYSELF_USER.getImageUrl(), view -> {
                //TODO : start the new activity for changing of name and image and about
                logcat("profile pic settings click");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.SettingsActivityFrameLayout,new ProfilePicFragment()).addToBackStack(null).commit();
            });
        }

        findPreference("preference_theme").setOnPreferenceChangeListener((preference, newValue) -> {
            logcat("seton preference change");
            switch(newValue.toString()){
                case "light":
                    logcat("light theme");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "dark":
                    logcat("dark");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                default:
                    logcat("system");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            return true;
        });


        findPreference("preference_logout").setOnPreferenceClickListener(preference -> {
            logout();
            return false;
        });

        findPreference("preference_clear").setOnPreferenceClickListener(preference -> {
            DB.MessageTableDao().deleteAllMessages();
            makeToast(requireContext(),"Data Cleared");
            return false;
        });


        try {
            String versionName = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0).versionName;
            findPreference("preference_version").setSummary("v"+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void logout() {
        FireBaseClass.auth().signOut();
        requireActivity().finish();
        requireActivity().finishAffinity();
    }
}
