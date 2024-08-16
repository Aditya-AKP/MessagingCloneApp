package com.akpdeveloper.baatcheet.fragments.settingsfragments;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.StartActivity.makeToast;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.MYSELF_USER;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.getProfileStorage;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.myUserUID;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akpdeveloper.baatcheet.MainActivity;
import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfilePicFragment extends Fragment {

    ActivityResultLauncher<Intent> resultLauncher;
    View v;
    ImageView imageView;
    TextView NameText;
    TextView AboutText;

    public ProfilePicFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if(result.getData()!=null && result.getData().getData() != null) {
                            Uri url = result.getData().getData();
                            imageView.setImageURI(url);
                            uploadImage(url);
                        }
                    }
                });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v = inflater.inflate(R.layout.fragment_profile_pic, container, false);

        initViews();
        startProgress(false);

        return v;
    }

    private void initViews(){

        v.findViewById(R.id.ProfilePicFragBack).setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        imageView = v.findViewById(R.id.ProfilePicFragImage);
        if(MYSELF_USER.getImageUrl()!=null) {
            Picasso.get().load(MYSELF_USER.getImageUrl()).into(imageView);
        }else {
            imageView.setImageResource(R.drawable.baseline_account_circle_24);
        }
        imageView.setOnClickListener(view -> {
            if(MainActivity.isMediaPermissionGranted){
                getTheImage();
            }else{
                makeToast(requireContext(),"Allow Media Permission");
            }
        });

        NameText = v.findViewById(R.id.ProfilePicFragName);
        NameText.setHint(MYSELF_USER.getName());
        AboutText = v.findViewById(R.id.ProfilePicFragAbout);
        AboutText.setHint(MYSELF_USER.getAbout());

        v.findViewById(R.id.ProfilePicFragSubmit).setOnClickListener(view -> {
            updateTheProfile();
        });
    }

    private void getTheImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivity(intent);
    }

    private void uploadImage(Uri url) {
        startProgress(true);
        UploadTask uploadTask = getProfileStorage(myUserUID()).putFile(url);
        uploadTask.continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        logcat("Task is failed : "+task.getException());
                        makeToast(requireContext(),"Something went wrong");
                    }
                    return getProfileStorage(myUserUID()).getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Map<String, Object> update = new HashMap<>();
                        update.put("imageUrl",task.getResult().toString());
                        FireBaseClass.allUsersCollectionReference().document(myUserUID()).update(update).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                startProgress(false);
                            }
                        }).addOnFailureListener(e -> {
                            startProgress(false);
                            makeToast(requireContext(),"Something went wrong");
                        });
                    }else{
                        makeToast(requireContext(),"Failed to upload image");
                        startProgress(false);
                    }

                })
                .addOnFailureListener(e -> {
                    logcat("Image Upload Failed : "+e.getMessage());
                    makeToast(requireContext(),"Not able to upload Image");
                    startProgress(false);
                });
    }
    private void updateTheProfile(){
        Map<String,Object> update = new HashMap<>();
        if(NameText.length()<3 && NameText.length()>0){
            NameText.setError(getString(R.string.Set_Up_name_cannot_be_less_than));
            return;
        } else if (NameText.length()>0) {
            update.put("name",NameText.getText().toString());
        }
        if(AboutText.length()>0){
            update.put("about",AboutText.getText().toString());
        }
        startProgress(true);
        FireBaseClass.allUsersCollectionReference().document(myUserUID()).update(update).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                startProgress(false);
            }
        }).addOnFailureListener(e->{
            startProgress(false);
        });
    }

    private void startProgress(boolean isInProgress){
        if(isInProgress){
            v.findViewById(R.id.ProfilePicFragProgress).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ProfilePicFragSubmit).setVisibility(View.GONE);
            v.findViewById(R.id.ProfilePicFragBack).setVisibility(View.INVISIBLE);
        }else{
            v.findViewById(R.id.ProfilePicFragProgress).setVisibility(View.GONE);
            v.findViewById(R.id.ProfilePicFragSubmit).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ProfilePicFragBack).setVisibility(View.VISIBLE);
        }
    }

}