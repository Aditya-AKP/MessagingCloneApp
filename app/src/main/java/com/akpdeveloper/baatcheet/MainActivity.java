package com.akpdeveloper.baatcheet;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_NUMBER;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_SEND_LOC;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_URL;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MULTIPLE_MEDIA;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_SINGLE_MEDIA;
import static com.akpdeveloper.baatcheet.SettingsActivity.setTheAppTheme;
import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.databases.AppDatabase.MIGRATION_6_7;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.akpdeveloper.baatcheet.adapter.MainActivityAdapter;
import com.akpdeveloper.baatcheet.databases.AppDatabase;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.utilities.AndroidUtils;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.akpdeveloper.baatcheet.utilities.MyPermissionClass;
import com.akpdeveloper.baatcheet.databinding.ActivityMainBinding;
import com.akpdeveloper.baatcheet.utilities.UploadAndDownloadFile;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static boolean isContactPermissionGranted = false;
    public static boolean isMediaPermissionGranted = false;
    public static boolean isNotificationPermissionGranted=false;
    private MyPermissionClass contactPermission;
    private MyPermissionClass mediaPermission;
    private MyPermissionClass notificationPermission;

    private Handler handler;
    private Runnable runnable;
    private String ImageURL;

    private synchronized void getDB(){
        DB = Room.databaseBuilder(this, AppDatabase.class,"BhaatCheetDB")
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_6_7)
                .fallbackToDestructiveMigration()
                .build();
    }

    private void getMyself(){
        FireBaseClass.allUsersCollectionReference().document(FireBaseClass.myUserUID()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                AndroidUtils.MYSELF_USER = task.getResult().toObject(UserModel.class);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logcat("start onCreate MainActivity");
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            finish();
        }
        setTheAppTheme(this);
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);

        getDB();
        getMyself();

        binding.mainViewPager.setAdapter(new MainActivityAdapter(this));
        new TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager, true,(tab, position) -> {
           if (position==0){
               tab.setText(getString(R.string.Main_Activity_chats));
           }else if(position==3){
               tab.setText(getString(R.string.Main_Activity_group));
           }else if(position==1){
               tab.setText(getString(R.string.Main_Activity_people));
           } else if (position==2) {
               tab.setText(getString(R.string.Main_Activity_status));
           }
        }).attach();

        requestPermission();

        if(getIntent()!=null && getIntent().getAction()!=null && getIntent().hasExtra(Intent.EXTRA_STREAM)){
            logcat("not null intent");
            if(Intent.ACTION_SEND.equals(getIntent().getAction())){
                Parcelable p;
                Intent intent = new Intent(this, ShareActivity.class);
                p = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
                intent.putExtra(MY_MEDIA_URL,p);
                intent.putExtra(MY_MEDIA_NUMBER,MY_SINGLE_MEDIA);
                intent.putExtra(MY_MEDIA_SEND_LOC,"mainActivity");
                startActivity(intent);
            }
            if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())) {
                ArrayList<Parcelable> list = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                Intent intent = new Intent(this, ShareActivity.class);
                intent.putExtra(MY_MEDIA_URL,list);
                intent.putExtra(MY_MEDIA_NUMBER,MY_MULTIPLE_MEDIA);
                intent.putExtra(MY_MEDIA_SEND_LOC,"mainActivity");
                startActivity(intent);
            }
        }else{
            logcat("null intent");
        }

//        handler = new Handler(getMainLooper());
//        runnable = () -> {
////            logcat("run : "+ "now : "+Timestamp.now()+" sec: "+Timestamp.now().getSeconds()+" Date : "+Timestamp.now().toDate()+" gettime: "+Timestamp.now().toDate().getTime()+" Day : "+Timestamp.now().toDate().getDay()+" Date2: "+Timestamp.now().toDate().getDate()+" hours: "+Timestamp.now().toDate().getHours());
////            handler.postDelayed(runnable,20000);
////            logcat("starting glide");
////            Glide.with(this).load(ImageURL).into((ImageView) findViewById(R.id.JustForExp));
//        };

        logcat("end onCreate MainActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        logcat("onStart Main Activity");
//        handler.postDelayed(runnable,5000);
//        new UploadAndDownloadFile(this,"https://firebasestorage.googleapis.com/v0/b/messanger-91581.appspot.com/o/uploads%2FAdq4WMJkHVhLqMdNp7zUAa9Mamb2%2Fprofile_photo?alt=media&token=05430934-4011-46a8-bb79-611393cd81d2")
//        {
//
//            @Override
//            public void completed(Context context, String url) {
//                logcat("completed");
//                logcat("URL: "+url);
//                ImageURL = url;
//            }
//        }.downloadMedia();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_more_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.more_menu_settings){
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }



    private void requestPermission(){
        //CONTACT Permission
        String[] wantedContactPermission = {READ_CONTACTS};
        contactPermission = new MyPermissionClass(this,wantedContactPermission,"Contacts","To chat with family and friends, Please provide contact permissions.") {
            @Override
            protected void granted() {
                isContactPermissionGranted=true;
            }

            @Override
            protected void notGranted() {
                super.notGranted();
                isContactPermissionGranted=false;
            }
        };

        //MEDIA Permission
        String[] wantedMediaPermission = getWantedMediaPermission();
        mediaPermission = new MyPermissionClass(this,wantedMediaPermission,"Media","Media permission require to send or receive media") {
            @Override
            protected void granted() {isMediaPermissionGranted = true;}
        };

        //NOTIFICATION Permission
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            String [] wantedNotificationPermission = {POST_NOTIFICATIONS};
            notificationPermission = new MyPermissionClass(this,wantedNotificationPermission,"Notification","to get notifiation") {
                @Override
                protected void granted() {
                    isNotificationPermissionGranted=true;
                }
            };
        }
    }

    @NonNull
    private static String[] getWantedMediaPermission() {
        String[] wantedMediaPermission;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            wantedMediaPermission = new String[]{READ_MEDIA_VISUAL_USER_SELECTED,READ_MEDIA_IMAGES,READ_MEDIA_VIDEO,READ_MEDIA_AUDIO};
        }else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU){
            wantedMediaPermission = new String[]{READ_MEDIA_IMAGES,READ_MEDIA_VIDEO,READ_MEDIA_AUDIO};
        }else{
            wantedMediaPermission = new String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE};
        }
        return wantedMediaPermission;
    }

    public void startCheckingForPermission(){
        contactPermission.start();
        mediaPermission.start();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)notificationPermission.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCheckingForPermission();
        logcat("onResume main activity");
    }
}