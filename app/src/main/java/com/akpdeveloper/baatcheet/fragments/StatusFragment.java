package com.akpdeveloper.baatcheet.fragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static com.akpdeveloper.baatcheet.SplashActivity.logcat;
import static com.akpdeveloper.baatcheet.SplashActivity.makeToast;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.randomUniqueID;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.getProfileStorage;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.getStatusStorage;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.myUserUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.adapter.StatusFragmentAdapter;
import com.akpdeveloper.baatcheet.enums.StatusType;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.StatusModel;
import com.akpdeveloper.baatcheet.utilities.AndroidUtils;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.akpdeveloper.baatcheet.utilities.MyPermissionClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusFragment extends Fragment {

    private ValueEventListener statusValueListener;

    private MyPermissionClass imagePermission,videoPermission;
    ActivityResultLauncher<Intent> imageLauncher,videoLauncher;

    private Uri mediaUrl;

    private List<String> statusSenderID;
    private List<Integer> noOfStatus;

    private List<DateModel> statusDate;

    private StatusFragmentAdapter adapter;
    private TextView noText;
    private RecyclerView rv;
    private ProgressBar progressBar;

    List<String> receiver;

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusDate = new ArrayList<>();
        statusSenderID = new ArrayList<>();
        noOfStatus = new ArrayList<>();
        receiver = new ArrayList<>();
        setActivityLauncher();
//        initializePermission();
        eventValueListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);


        initView(view);
        setTheRecyclerView();
        return view;
    }

    private void initView(View v){
        //TODO: initialize views
        noText = v.findViewById(R.id.StatusFragNoText);
        rv = v.findViewById(R.id.StatusFragRecyclerView);
        progressBar = v.findViewById(R.id.StatusFragProgress);
        v.findViewById(R.id.StatusFragFAB).setOnClickListener(view -> {
            askForStatusType();
        });
    }

    private void eventValueListener(){
        statusValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusSenderID.clear();
                noOfStatus.clear();
                Map<String,Integer> numberOfSender = new HashMap<>();
                GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {};
                List<StatusModel> statusModelList = new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren()){
                    //add status to the status model list

                    if(ds.child("receiverIDs").getValue(typeIndicator)!=null
                    && !ds.child("receiverIDs").getValue(typeIndicator).contains(FireBaseClass.myUserUID())){
                        continue;
                    }

                    String senderId = ds.child("senderID").getValue(String.class);
                    statusModelList.add(new StatusModel(
                            ds.child("url").getValue(String.class),
                            ds.child("text").getValue(String.class),
                            ds.child("date").getValue(DateModel.class),
                            senderId,
                            ds.child("statusType").getValue(StatusType.class),
                            null
                    ));
                    if(numberOfSender.containsKey(senderId)){
                        Integer number = numberOfSender.get(senderId);
                        numberOfSender.put(senderId,number+1);
                    }else{
                        numberOfSender.put(senderId,1);
                        statusDate.add(ds.child("date").getValue(DateModel.class));
                    }
                }
                if(statusModelList.isEmpty()){
                    //TODO : enable the no status text in fragment
                    noText.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                }else{
                    //TODO : update the recycler view
                    noText.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                   numberOfSender.forEach((String k,Integer v)->{
                       statusSenderID.add(k);
                       noOfStatus.add(v);
                   });
                    adapter.notifyItemRangeChanged(0,statusSenderID.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                logcat("status onCancelled error: "+error.getMessage());
            }
        };
    }


    private void setTheRecyclerView(){
        adapter = new StatusFragmentAdapter(requireContext(),statusDate,statusSenderID,noOfStatus);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
    }

    private void askForStatusType() {
        String[] type = {"Text","Photo","Video"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Status Type")
                .setItems(type, (dialog,position)->{
                    switch (position){
                        case 0:getStatusText(StatusType.TEXT);break;
                        case 1:Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            imageLauncher.launch(intent);
//                            imagePermission.start();
                            break;
                        case 2:Intent intent1 = new Intent(Intent.ACTION_PICK);
                            intent1.setType("video/*");
                            videoLauncher.launch(intent1);
//                            videoPermission.start();
                            break;
                    }
                    dialog.dismiss();
                })
                .setCancelable(true)
                .create()
                .show();
    }

    private void uploadStatusMedia(StatusType statusType, String text){
        setProgressBarRunning(true);
        switch (statusType){
            case TEXT:
                uploadStatusDetail(statusType,text,"");
                break;
            case VIDEO:
            case IMAGE:
                String path = randomUniqueID();
                UploadTask uploadTask = getStatusStorage(myUserUID()).child(path).putFile(mediaUrl);
                uploadTask.continueWithTask(task -> {
                            if(!task.isSuccessful()){
                                logcat("Task is failed : "+task.getException());
                                makeToast(requireContext(),"Something went wrong");
                            }
                            logcat("continue with task");
                            return getStatusStorage(myUserUID()).child(path).getDownloadUrl();
                        })
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                uploadStatusDetail(statusType,text,task.getResult().toString());
                            }else{
                                makeToast(requireContext(),"Failed to upload Media");
                                setProgressBarRunning(false);
                            }

                        })
                        .addOnFailureListener(e -> {
                            logcat("Image Upload Failed : "+e.getMessage());
                            makeToast(requireContext(),"Not able to upload Media");
                            setProgressBarRunning(false);
                        });
                break;
        }
    }

    private void uploadStatusDetail(StatusType type,String text,String url){
        StatusModel statusModel = new StatusModel(url,text,DateModel.now(),myUserUID(),type,receiver);

        FireBaseClass.getStatusCollectionReference().push().setValue(statusModel).addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               logcat("status upload successfully");
               makeToast(requireContext(),"status uploaded");
           }else{
               logcat("failed status upload");
               makeToast(requireContext(),"something went wrong");
           }
            setProgressBarRunning(false);
        })
                .addOnFailureListener(e -> {
                    logcat("status upload failed : "+e.getMessage());
                    makeToast(requireContext(),"something went wrong");
                    setProgressBarRunning(false);
                });
    }

    private void getStatusText(StatusType statusType){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        EditText input = new EditText(requireContext());
        builder.setTitle("Enter Text");
        builder.setView(input);
        builder.setPositiveButton("Upload Status",(dialogInterface, i) -> {
            uploadStatusMedia(statusType,input.getText().toString());
        });
        builder.setNegativeButton("Cancel Status",(dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.show();
    }

    private void setProgressBarRunning(boolean isRun){
        if(isRun){progressBar.setVisibility(View.VISIBLE);}
        else {progressBar.setVisibility(View.GONE);}
    }

    private void setActivityLauncher(){
        imageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if(result.getData()!=null && result.getData().getData() != null) {
                            mediaUrl = result.getData().getData();
                            getStatusText(StatusType.IMAGE);
                        }
                    }
                });
        videoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if(result.getData()!=null && result.getData().getData() != null) {
                            mediaUrl = result.getData().getData();
                            getStatusText(StatusType.VIDEO);
                        }
                    }
                });
    }

    private void initializePermission(){
        String[] imagePer,videoPer;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            imagePer = new String[]{READ_MEDIA_VISUAL_USER_SELECTED,READ_MEDIA_IMAGES};
            videoPer = new String[]{READ_MEDIA_VISUAL_USER_SELECTED,READ_MEDIA_VIDEO};
        }else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU){
            imagePer = new String[]{READ_MEDIA_IMAGES};
            videoPer = new String[]{READ_MEDIA_VIDEO};
        }else{
            imagePer = new String[]{READ_EXTERNAL_STORAGE};
            videoPer = new String[]{READ_EXTERNAL_STORAGE};
        }

        imagePermission = new MyPermissionClass((AppCompatActivity) requireContext(),imagePer,"Images","To Select Image for status, Please allow permission for Images") {
            @Override
            protected void granted() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imageLauncher.launch(intent);
            }
        };

        videoPermission = new MyPermissionClass((AppCompatActivity) requireContext(),videoPer,"Videos","To Select Video for status, Please allow permission for Videos") {
            @Override
            protected void granted() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                videoLauncher.launch(intent);
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        FireBaseClass.getStatusCollectionReference().addValueEventListener(statusValueListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        FireBaseClass.getStatusCollectionReference().removeEventListener(statusValueListener);
    }
}