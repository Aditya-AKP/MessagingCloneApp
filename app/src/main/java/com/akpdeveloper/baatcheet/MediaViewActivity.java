package com.akpdeveloper.baatcheet;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.StartActivity.makeToast;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.akpdeveloper.baatcheet.databinding.ActivityMediaViewBinding;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.utilities.AndroidUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class MediaViewActivity extends AppCompatActivity {

    public static final String MY_MEDIA_URL = "mediaUrls";
    public static final String MY_SINGLE_MEDIA = "singleMedia";
    public static final String MY_MULTIPLE_MEDIA = "multipleMedia";
    public static final String MY_MEDIA_NUMBER = "mediaNumber";
    public static final String MY_MEDIA_SEND_LOC = "mediaSendLoc";

    private ArrayList<String> urls;
    private int index = 0;
    private Intent intent;

    ActivityMediaViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = ActivityMediaViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(),getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

//        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(),((v, insets) -> {
//            if(insets.isVisible(WindowInsetsCompat.Type.navigationBars()))
//            return insets;
//        }));

        binding.MediaViewActImageView.setMaxZoom(5f);
        urls = new ArrayList<>();
        intent = new Intent(this, ChatRoomActivity.class);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.MediaViewActVideoView);
        mediaController.setMediaPlayer(binding.MediaViewActVideoView);
        binding.MediaViewActVideoView.setMediaController(mediaController);
        binding.MediaViewActVideoView.setOnPreparedListener(mediaPlayer -> {
            binding.MediaViewActVideoView.start();
        });

        if(Objects.equals(getIntent().getStringExtra(MY_MEDIA_NUMBER), MY_SINGLE_MEDIA)){
            intent.putExtra(MY_MEDIA_NUMBER,MY_SINGLE_MEDIA);
            if(getIntent().getStringExtra(MY_MEDIA_URL)!=null) {
                urls.add(getIntent().getStringExtra(MY_MEDIA_URL));
                intent.putExtra(MY_MEDIA_URL,getIntent().getStringExtra(MY_MEDIA_URL));
            }else if(getIntent().getParcelableExtra(MY_MEDIA_URL)!=null){
                urls.add(getIntent().getParcelableExtra(MY_MEDIA_URL).toString());
                intent.putExtra(MY_MEDIA_URL,(Parcelable) getIntent().getParcelableExtra(MY_MEDIA_URL));
            }else{
                logcat("finish media view activity");
                makeToast(this,"Media not selected");
                finish();
            }
            binding.MediaViewActLeftButton.setVisibility(View.GONE);
            binding.MediaViewActRightButton.setVisibility(View.GONE);
        }else if (Objects.equals(getIntent().getStringExtra(MY_MEDIA_NUMBER), MY_MULTIPLE_MEDIA)) {
            logcat("starting parsing");
            intent.putExtra(MY_MEDIA_NUMBER,MY_MULTIPLE_MEDIA);
            intent.putExtra(MY_MEDIA_URL,getIntent().getParcelableArrayListExtra(MY_MEDIA_URL));
            ArrayList<Parcelable> list = getIntent().getParcelableArrayListExtra(MY_MEDIA_URL);
            if(list!=null) {
                for (Parcelable i : list) {
                    urls.add(i.toString());
                }
            }else{
                logcat("finish media view activity");
                makeToast(this,"Media not selected");
                finish();
            }
        }else{
            logcat("finish media view activity");
            makeToast(this,"Media not selected");
            finish();
        }

        if(getIntent().hasExtra(MY_MEDIA_SEND_LOC)){
            binding.MediaViewActSendButton.setVisibility(View.VISIBLE);
        }


        showMedia();


        binding.MediaViewActLeftButton.setOnClickListener(view -> {
            showPreviousMedia();
        });
        binding.MediaViewActRightButton.setOnClickListener(view -> {
            showNextMedia();
        });
        binding.MediaViewActSendButton.setOnClickListener(view -> {
            UserModel userModel = AndroidUtils.getUserModelFromIntent(getIntent());
            if(userModel.getuID()==null){
                logcat("user not found");
                makeToast(this,"User not found");
                return;
            }
            AndroidUtils.setUserModelToIntent(intent,userModel);
            startActivity(intent);
        });
    }

    private void showMedia(){
        binding.MediaViewActImageView.setVisibility(View.GONE);
        binding.MediaViewActVideoView.setVisibility(View.GONE);
        if(urls.get(index).contains(".com") || urls.get(index).contains("http")){
            binding.MediaViewActImageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .asGif()
                    .load(urls.get(index))
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.baseline_error_24)
                    .into(binding.MediaViewActImageView);
        }else{
            String mime = getContentResolver().getType(Uri.parse(urls.get(index)));
            if(mime==null){
                makeToast(this,"Media not Found");
                return;
            }
            if(mime.contains("image")){
                binding.MediaViewActImageView.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(urls.get(index))
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.baseline_error_24)
                        .into(binding.MediaViewActImageView);
            } else if (mime.contains("video")) {
                binding.MediaViewActVideoView.setVisibility(View.VISIBLE);
                binding.MediaViewActVideoView.setVideoPath(urls.get(index));
            }else{
                binding.MediaViewActImageView.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .asBitmap()
                        .load(urls.get(index))
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.baseline_error_24)
                        .into(binding.MediaViewActImageView);
            }
        }
    }

    private void showPreviousMedia(){
        if(index<=0){
            binding.MediaViewActLeftButton.setEnabled(false);
            return;
        }
        index--;
        binding.MediaViewActRightButton.setEnabled(true);
        showMedia();
    }
    private void showNextMedia(){
        if(index>=urls.size()-1){
            binding.MediaViewActRightButton.setEnabled(false);
            return;
        }
        index++;
        binding.MediaViewActLeftButton.setEnabled(true);
        showMedia();
    }
}