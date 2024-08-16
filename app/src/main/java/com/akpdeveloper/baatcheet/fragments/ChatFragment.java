package com.akpdeveloper.baatcheet.fragments;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akpdeveloper.baatcheet.adapter.ChatFragmentAdapter;
import com.akpdeveloper.baatcheet.databases.ChatTable;
import com.akpdeveloper.baatcheet.models.ChatModel;
import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.utilities.ChatModelComparator;
import com.akpdeveloper.baatcheet.models.MessageModel;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatFragmentAdapter adapter;

    ValueEventListener chatValueEventListen;

    private  List<ChatModel> chatModelList;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing chat model list
        chatModelList = new ArrayList<>();
        //setting up the value event listener for recycler view
        setValueEventListener();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
//        FireBaseClass.allChatCollectionReference()
//                .whereArrayContains("userIDs",FireBaseClass.myUserUID())
//                .orderBy("lastTimestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
//                    if(task.isSuccessful()){
//                        List<ChatModel> chatModelList = new ArrayList<>(task.getResult().toObjects(ChatModel.class));
//                        setRecyclerView(view,chatModelList);
//                    }
//                });
//        FireBaseClass.allChatCollectionReference().orderByChild("userIDs").co

        //setting the recycler view
        setRecyclerView(view);
        return view;

    }

    private void setValueEventListener(){
       chatValueEventListen =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear the chat model list
                chatModelList.clear();
                //type indicator to get the chat room of my user id from firebase
                GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {};
                for(DataSnapshot ds:snapshot.getChildren()){
                    //getting user id list in chat model from firebase using type indicator
                    List<String> userid = ds.child("userIDs").getValue(typeIndicator);
                    //continue if my user id not contain in it
                    if(userid!=null && !userid.contains(FireBaseClass.myUserUID())){
                        continue;
                    }
                    //creating chat model and add to the chat model list
                    ChatModel cm = new ChatModel(
                            ds.child("chatID").getValue(String.class),
                            userid,
                            ds.child("lastMessage").getValue(MessageModel.class),
                            ds.child("newMessageNumber").getValue(Integer.class)
                    );
                    chatModelList.add(cm);
                }
                if(!chatModelList.isEmpty()){
                    logcat("chat model list in firebase is empty");
                }
                logcat("size of chat model list : "+chatModelList.size());
                //sort the chat model according to its last message timestamp
                chatModelList.sort(new ChatModelComparator());
                //save Chat Model in room db
                for(ChatModel i:chatModelList){
                    ChatTable ct = new ChatTable(i.getChatID(),i.getUserIDs().get(0),i.getUserIDs().get(1),i.getLastMessage(),i.getNewMessageNumber());
                    DB.ChatTableDao().saveChat(ct);
                }
                adapter.setUpdateFromFirebase(true);
                adapter.notifyItemRangeChanged(0,chatModelList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                logcat("onCancelled 32");
            }
        };
    }

    private void getFromRoomDB(){
        //getting chat models from room db
        List<ChatTable> chatTables = DB.ChatTableDao().getAllChats();
        logcat("Chat number"+chatTables.size());
        //add chat model in chat model list
        for(ChatTable i:chatTables){
            chatModelList.add(new ChatModel(i.getChatID(),new ArrayList<>(Arrays.asList(i.getUser1(),i.getUser2())),i.getLastMessage(),i.getNewMessageNumber()));
        }
        chatModelList.sort(new ChatModelComparator());
        if(!chatModelList.isEmpty()){
            logcat("chat model size: "+chatModelList.size());
        }else{
            logcat("no chat model");
        }
    }

    private void setRecyclerView(@NonNull View v){
        //getting chat model from room db
        getFromRoomDB();
        //setting the recycler view
        adapter = new ChatFragmentAdapter(requireContext(),chatModelList);
        RecyclerView recyclerView = v.findViewById(R.id.ChatFragRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onStart() {
        FireBaseClass.allChatCollectionReference().addValueEventListener(chatValueEventListen);
        logcat("onStart chatFragment");
        super.onStart();
    }

    @Override
    public void onStop() {
        FireBaseClass.allChatCollectionReference().removeEventListener(chatValueEventListen);
        logcat("onStop chatFragment");
        super.onStop();
    }
}