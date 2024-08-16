package com.akpdeveloper.baatcheet.receiver;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DownloadReceiver extends BroadcastReceiver {
    public static final Map<String,String> pendingDownloadMedia = new HashMap<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), "android.intent.action.DOWNLOAD_COMPLETE")){
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1L);
            if(id!=-1L){
                logcat("download complete");
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
                Cursor cur = downloadManager.query(query);
                if(cur.moveToFirst()){
                    if(cur.getCount()>0){
                        int col1 = cur.getColumnIndex(DownloadManager.COLUMN_URI);
                        int col2 = cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        if(col1!=-1 && col2!=-1){
                            String url = cur.getString(col1);
                            String local_uri = cur.getString(col2);
                            downloadComplete(url,local_uri);
                            FirebaseStorage.getInstance().getReferenceFromUrl(url).delete();
                            logcat("download uri: "+cur.getString(col1));
                            logcat("download local uri: "+cur.getString(col2));
                        }
                    }
                }
            }
        }
    }

    private void downloadComplete(String oldURL,String newURL){
        String id = pendingDownloadMedia.get(oldURL);
        DB.MessageTableDao().updateMessageURL(id,newURL);
    }
}
