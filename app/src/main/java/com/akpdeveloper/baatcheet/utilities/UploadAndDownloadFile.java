package com.akpdeveloper.baatcheet.utilities;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.StartActivity.makeToast;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.getMediaStorage;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.getProfileStorage;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.myUserUID;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.ProgressBar;

import com.akpdeveloper.baatcheet.enums.MessageType;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public abstract class UploadAndDownloadFile {

    private final Context context;
    private final String url;
    private final MessageType type;
    private final String mime;

    public UploadAndDownloadFile(Context context,String url,MessageType type){
        this.context = context;
        this.url = url;
        this.type = type;
        StringBuilder sb = new StringBuilder();
        for(int i=url.length()-1;i>=0;i--){
            sb.append(url.charAt(i));
        }
        mime = sb.reverse().toString();
    }
    public UploadAndDownloadFile(Context context,String url,MessageType type,String mimeType){
        this.context = context;
        this.url = url;
        this.type = type;
        StringBuilder sb = new StringBuilder();
        for(int i=mimeType.length()-1;i>=0;i--){
            if (mimeType.charAt(i)=='.'){
                break;
            }
            sb.append(mimeType.charAt(i));
        }
        mime = sb.reverse().toString();
    }

    public void uploadProfilePhoto(){
        preConnection(context);
        UploadTask uploadTask = FireBaseClass.getProfileStorage(myUserUID()).putFile(Uri.parse(url));
        uploadTask.continueWithTask(task -> {
            if(!task.isSuccessful()){
                logcat("Task is failed : "+task.getException());
                makeToast(context,"Upload failed");
            }
            return getProfileStorage(myUserUID()).getDownloadUrl();
        })
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        uploadCompleted(context, task.getResult().toString(),mime);
                    }else{
                        failure(context,new Exception("task is unsuccessful"));
                    }
                    postConnection(context);
                })
                .addOnFailureListener(e ->{
                    failure(context,e);
                    postConnection(context);
                });
    }

    public void uploadMedia(){
        preConnection(context);
        String pathName = "Media_"+System.currentTimeMillis();
        UploadTask uploadTask = FireBaseClass.getMediaStorage(myUserUID()).child(pathName).putFile(Uri.parse(url));
        uploadTask.continueWithTask(task -> {
            if(!task.isSuccessful()){
                logcat("Task is Failed: "+task.getException());
                makeToast(context,"Upload Failed");
            }
            return getMediaStorage(myUserUID()).child(pathName).getDownloadUrl();
        })
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        uploadCompleted(context,task.getResult().toString(),mime);
                    }else{
                        failure(context,new Exception("Task is Unsuccessful"));
                    }
                    postConnection(context);
                })
                .addOnFailureListener(e -> {
                    failure(context,e);
                    postConnection(context);
                });
    }

    public void downloadMedia(){
        preConnection(context);
        logcat("Downloading Media");

        String folder;
        switch (type){
            case IMAGE:
                folder = "Images";
                break;
            case VIDEO:
                folder = "Videos";
                break;
            case STICKER:
                folder = "Stickers";
                break;
            case FILE:
            default:folder = "Files";
        }
        File fileLocation = new File(Environment.getExternalStorageDirectory().toString(),"/Download/Baatcheet/"+folder+"/");
        if(!fileLocation.exists()){
            if(!fileLocation.mkdirs()) {
                makeToast(context, "Failed to create directory");
                failure(context,new Exception("Directory not found"));
            }
        }

        String fileName;
        if(type==MessageType.STICKER){
            fileName = "BaatCheet_"+System.currentTimeMillis();
        }else{
            fileName = "BaatCheet_"+System.currentTimeMillis()+"."+mime;
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Baatcheet/"+folder+"/"+fileName);
        downloadManager.enqueue(request);
        logcat("File Location: "+fileLocation);
        logcat("FileName: "+fileName);
        logcat("sett External Directory: "+Environment.DIRECTORY_DOWNLOADS+"Baatcheet/"+fileName);
        downloadCompleted(context,Environment.getExternalStorageDirectory()+"/Download/Baatcheet/"+folder+"/"+fileName);
        postConnection(context);
    }

    public void downloadCompleted(Context context,String url){};
    public void uploadCompleted(Context context,String url,String mime){};

    public void failure(Context context, Exception e){};
    public void preConnection(Context context){};
    public void postConnection(Context context){};

}
