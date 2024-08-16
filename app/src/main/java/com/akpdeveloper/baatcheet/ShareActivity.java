package com.akpdeveloper.baatcheet;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akpdeveloper.baatcheet.adapter.ChatFragmentAdapter;
import com.akpdeveloper.baatcheet.adapter.ShareActivityAdapter;
import com.akpdeveloper.baatcheet.databases.ChatTable;
import com.akpdeveloper.baatcheet.databinding.ActivityShareBinding;
import com.akpdeveloper.baatcheet.models.ChatModel;
import com.akpdeveloper.baatcheet.utilities.ChatModelComparator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    ActivityShareBinding binding;
    private ShareActivityAdapter adapter;

    private ArrayList<ChatModel> chatModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatModelList = new ArrayList<>();
        setRecyclerView();

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

    private void setRecyclerView(){
        //getting chat model from room db
        getFromRoomDB();
        //setting the recycler view
        adapter = new ShareActivityAdapter(this,chatModelList,getIntent());
        binding.ShareActRecyclerView.setAdapter(adapter);
        binding.ShareActRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}