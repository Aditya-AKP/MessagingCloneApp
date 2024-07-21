package com.akpdeveloper.baatcheet;

import static com.akpdeveloper.baatcheet.SplashActivity.logcat;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.akpdeveloper.baatcheet.databinding.ActivityStatusBinding;
import com.akpdeveloper.baatcheet.enums.StatusType;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.StatusModel;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.utilities.AndroidUtils;
import com.akpdeveloper.baatcheet.utilities.DateUtils;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.akpdeveloper.baatcheet.utilities.OnSwipeTouchListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatusActivity extends AppCompatActivity {

    List<StatusModel> statusModelList;

    ActivityStatusBinding binding;

    StatusModel currentStatus;
    int currentStatusPosition;
    String userID;
    boolean isShown=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        statusModelList = new ArrayList<>();

        userID = getIntent().getStringExtra("senderid");
        if(userID==null) endTheActivity();

        FireBaseClass.allUsersCollectionReference().document(userID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel userModel = task.getResult().toObject(UserModel.class);
                assert userModel != null;
                binding.StatusActProfileText.setText(userModel.getName());
                if(userModel.getImageUrl()!=null) {
                    Picasso.get().load(userModel.getImageUrl()).into(binding.StatusActProfileImage);
                }else{
                    binding.StatusActProfileImage.setImageResource(R.drawable.baseline_account_circle_24);
                }
            }
        });

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.StatusActVideo);
        mediaController.setMediaPlayer(binding.StatusActVideo);
        binding.StatusActVideo.setMediaController(mediaController);


        setTheTouchGesture();
        setTheGesture();

        getTheStatusFromFirebase();
    }


    private void getTheStatusFromFirebase(){
        FireBaseClass.getStatusCollectionReference().get().addOnCompleteListener(task ->{
           if(task.isSuccessful()){
               statusModelList.clear();
               GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {};
               for(DataSnapshot ds:task.getResult().getChildren()){
                   if(Objects.equals(ds.child("senderID").getValue(String.class), userID)) {
                       statusModelList.add(new StatusModel(
                               ds.child("url").getValue(String.class),
                               ds.child("text").getValue(String.class),
                               ds.child("date").getValue(DateModel.class),
                               ds.child("senderID").getValue(String.class),
                               ds.child("statusType").getValue(StatusType.class),
                               ds.child("receiverIDs").getValue(typeIndicator)
                       ));
                   }
               }

               if (statusModelList.isEmpty()) endTheActivity();
               else {
                   statusModelList.sort((t, t1) -> (int) (t.getDate().getSeconds() - t1.getDate().getSeconds()));
                   currentStatusPosition = 0;
                   showStatus();
               }
           }
        });
    }

    private void setTheTouchGesture() {}

    private void showStatus() {
        currentStatus = statusModelList.get(currentStatusPosition);
        switch (currentStatus.getStatusType()){
            case TEXT:  setTheText();
                break;
            case IMAGE: setTheImage();
                break;
            case VIDEO: setTheVideo();
                break;
        }
    }

    private void startPreviousStatus(){
        currentStatusPosition--;
        if(currentStatusPosition<0){
            endTheActivity();
        }else{
            showStatus();
        }
    }

    private void startNextStatus(){
        currentStatusPosition++;
        if(currentStatusPosition>=statusModelList.size()){
            endTheActivity();
        }else{
            showStatus();
        }
    }

    private void setTheText(){
        binding.StatusActVideo.setVisibility(View.GONE);
        binding.StatusActImage.setVisibility(View.GONE);
        binding.StatusActText.setVisibility(View.VISIBLE);

        binding.StatusActText.setText(currentStatus.getText());

        setTheCommonArea();
    }

    private void setTheImage(){
        binding.StatusActVideo.setVisibility(View.GONE);
        binding.StatusActImage.setVisibility(View.VISIBLE);
        binding.StatusActText.setVisibility(View.GONE);

        if(currentStatus.getUrl()!=null) {
            Picasso.get().load(currentStatus.getUrl()).into(binding.StatusActImage);
        }

        binding.StatusActDesText.setText(currentStatus.getText());

        setTheCommonArea();
    }

    private void setTheVideo(){
        binding.StatusActVideo.setVisibility(View.VISIBLE);
        binding.StatusActImage.setVisibility(View.GONE);
        binding.StatusActText.setVisibility(View.GONE);

        binding.StatusActVideo.setVideoURI(Uri.parse(currentStatus.getUrl()));
        binding.StatusActProgressBar.setVisibility(View.VISIBLE);
        binding.StatusActVideo.setOnPreparedListener(mediaPlayer -> {
            binding.StatusActProgressBar.setVisibility(View.INVISIBLE);
            binding.StatusActVideo.start();
        });
        binding.StatusActVideo.setOnCompletionListener(mediaPlayer -> {
            binding.StatusActVideo.seekTo(0);
            binding.StatusActVideo.start();
        });

        binding.StatusActDesText.setText(currentStatus.getText());

        setTheCommonArea();
    }

    private void setTheCommonArea(){
        binding.StatusActProfileDate.setText(DateUtils.getDateFromTimestamp(currentStatus.getDate()));
        String s = currentStatusPosition+1+"/"+statusModelList.size();
        binding.StatusActProfileNumber.setText(s);
    }

    private void startShowingStatusText(){
        if(isShown){
            binding.StatusActDesProfile.setVisibility(View.VISIBLE);
            binding.StatusActDesArea.setVisibility(View.VISIBLE);
        }else{
            binding.StatusActDesProfile.setVisibility(View.GONE);
            binding.StatusActDesArea.setVisibility(View.GONE);
        }
        isShown=!isShown;
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.StatusActVideo.stopPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.StatusActVideo.pause();
    }

    private void endTheActivity() {
        finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTheGesture() {
        binding.main.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                startPreviousStatus();
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                startNextStatus();
            }

            @Override
            public void onSingleClick() {
                super.onSingleClick();
                startShowingStatusText();
            }
        });
    }
}