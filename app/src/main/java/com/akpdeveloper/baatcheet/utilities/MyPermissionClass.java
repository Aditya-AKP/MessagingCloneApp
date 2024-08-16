package com.akpdeveloper.baatcheet.utilities;


import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.akpdeveloper.baatcheet.StartActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public abstract class MyPermissionClass {
    private final AppCompatActivity activity;
    private final ActivityResultLauncher<String[]> launcher;
    private final String[] permissionsList;
    private final String message;

    private final String permissionName;

    public MyPermissionClass(@NonNull AppCompatActivity activity, String[] permissionsList, String permissionName, String message) {
        this.activity=activity;
        this.permissionsList = permissionsList;
        this.message = message;
        this.permissionName=permissionName;
        this.launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), permissionStatusMap -> {
                    if (!permissionStatusMap.containsValue(false)){
                        granted();
                    }else{
                        notGranted();
                    }
        });
    }

    protected abstract void granted();
    protected void notGranted(){
        StartActivity.makeToast(activity,permissionName + " Permission Denied");}
    public void start(){
        if(isPermissionGranted() != PackageManager.PERMISSION_GRANTED){
            requestPermission();
        }else{
            granted();
        }
    }
    private int isPermissionGranted(){
        int counter =0;
        for (String per:permissionsList) {
            counter+=checkSelfPermission(activity,per);
        }
        return counter;
    }


    private void requestPermission(){
        String permission = deniedPermission();
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
            showDialogForPermissionRequest();
        }else{
            launcher.launch(permissionsList);
        }
    }

    private void showDialogForPermissionRequest(){
        new MaterialAlertDialogBuilder(activity)
                .setTitle("Permission Required")
                .setMessage(message)
                .setPositiveButton("Settings",(dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",activity.getPackageName(),null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                })
                .setCancelable(true)
                .create()
                .show();
    }

    private String deniedPermission(){
        for (String permission:permissionsList) {
            if(checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED){
                return permission;
            }
        }
        return "";
    }
}
