package com.akpdeveloper.baatcheet;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_CONTACTS;
import static com.akpdeveloper.baatcheet.SplashActivity.logcat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;

public class MainActivity extends AppCompatActivity {

    public static boolean isContactPermissionGranted = false;
    public static boolean isNotificationPermissionGranted=false;
    private static MyPermissionClass contactPermission;
    private static MyPermissionClass notificationPermission;

    private Handler handler;
    private Runnable runnable;

    private synchronized void getDB(){
        AndroidUtils.DB = Room.databaseBuilder(this, AppDatabase.class,"BhaatCheetDB")
                .allowMainThreadQueries()
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

        PopupMenu popupMenu = new PopupMenu(this,binding.mainToolbar);
        popupMenu.getMenuInflater().inflate(R.menu.main_activity_more_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.more_menu_logout) {
                logout();
            }
            return true;
        });


        requestPermission();

//        handler = new Handler(getMainLooper());
//        runnable = () -> {
//            logcat("run : "+ "now : "+Timestamp.now()+" sec: "+Timestamp.now().getSeconds()+" Date : "+Timestamp.now().toDate()+" gettime: "+Timestamp.now().toDate().getTime()+" Day : "+Timestamp.now().toDate().getDay()+" Date2: "+Timestamp.now().toDate().getDate()+" hours: "+Timestamp.now().toDate().getHours());
//            handler.postDelayed(runnable,20000);
//        };

        logcat("end onCreate MainActivity");
    }



    @Override
    protected void onStart() {
        super.onStart();
//        logcat("onStart Main Activity");
//        handler.postDelayed(runnable,5000);
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
        else if(item.getItemId()==R.id.more_menu_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FireBaseClass.auth().signOut();
        finish();
        finishAffinity();
    }

    private void requestPermission(){
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

    public static void requestForContactPermission(){contactPermission.start();}

    public static void requestForNotificationPermission(){if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)notificationPermission.start();}

    @Override
    protected void onResume() {
        super.onResume();
        requestForContactPermission();
        requestForNotificationPermission();
        logcat("onResume main activity");
    }
}