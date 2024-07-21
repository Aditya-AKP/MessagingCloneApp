package com.akpdeveloper.baatcheet;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static com.akpdeveloper.baatcheet.SplashActivity.logcat;
import static com.akpdeveloper.baatcheet.SplashActivity.makeToast;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.getProfileStorage;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.myUserUID;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.akpdeveloper.baatcheet.utilities.MyPermissionClass;
import com.akpdeveloper.baatcheet.databinding.ActivitySetUpBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class SetUpActivity extends AppCompatActivity {


    ActivityResultLauncher<Intent> someActivityResultLauncher;
    MyPermissionClass ImagePermission;
    ActivitySetUpBinding binding;
    String phoneNumber;
    String image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString(Intent.EXTRA_PHONE_NUMBER);

        startProgress(false);

        initButton();

        intiPermissions();

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if(result.getData()!=null && result.getData().getData() != null) {
                            Uri url = result.getData().getData();
                            image=url.toString();
                            binding.SetUpImage.setImageURI(url);
                            uploadImage(url);
                        }
                    }
                });

    }

    private void initButton(){
        binding.SetUpImage.setOnClickListener(view -> {
            ImagePermission.start();
        });

        binding.SetUpNextButton.setOnClickListener(view -> {
            String name = binding.SetUpName.getText().toString();
            if(name.length()<3){
                binding.SetUpName.setError(getString(R.string.Set_Up_name_cannot_be_less_than));
                return;
            }
            startProgress(true);
            setTheUserInFirebase(name);
        });
    }

    private void intiPermissions(){
        String[] permissionWanted;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            permissionWanted = new String[]{READ_MEDIA_VISUAL_USER_SELECTED,READ_MEDIA_IMAGES};
        }else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU){
            permissionWanted = new String[]{READ_MEDIA_IMAGES};
        }else{
            permissionWanted = new String[]{READ_EXTERNAL_STORAGE};
        }

        ImagePermission = new MyPermissionClass(this,permissionWanted,"Images","To Select Profile Image, Please allow permission for Images") {
            @Override
            protected void granted() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                someActivityResultLauncher.launch(intent);
            }
        };
    }

    private void setTheUserInFirebase(String name){

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               String token = task.getResult();
               String about = binding.SetUpAbout.getText().toString();
               if(about.isEmpty()){
                   about=getString(R.string.Set_Up_default_about);
               }
               UserModel user = new UserModel(name,about,myUserUID(),phoneNumber,image, DateModel.now(),token);
               FireBaseClass.allUsersCollectionReference()
                       .document(myUserUID())
                       .set(user)
                       .addOnCompleteListener(task1 -> {
                           Intent intent = new Intent(this, MainActivity.class);
                           finishAffinity();
                           startActivity(intent);
                       })
                       .addOnFailureListener(e -> {
                           startProgress(false);
                           makeToast(this,"Something went wrong");
                       });
           }
        });

    }

    private void uploadImage(Uri url){
        startProgress(true);
        UploadTask uploadTask = getProfileStorage(myUserUID()).putFile(url);
        uploadTask.continueWithTask(task -> {
            if(!task.isSuccessful()){
                logcat("Task is failed : "+task.getException());
                makeToast(this,"Something went wrong");
            }
            return getProfileStorage(myUserUID()).getDownloadUrl();
        })
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        image = task.getResult().toString();
                    }else{
                        makeToast(this,"Failed to upload image");
                    }
                    startProgress(false);
                })
                .addOnFailureListener(e -> {
                    logcat("Image Upload Failed : "+e.getMessage());
                    makeToast(this,"Not able to upload Image");
                    startProgress(false);
                });
    }

    private void startProgress(boolean isInProgress){
        if(isInProgress){
            binding.SetUpProgressBar.setVisibility(View.VISIBLE);
            binding.SetUpNextButton.setVisibility(View.GONE);
        }else{
            binding.SetUpProgressBar.setVisibility(View.GONE);
            binding.SetUpNextButton.setVisibility(View.VISIBLE);
        }
    }

}