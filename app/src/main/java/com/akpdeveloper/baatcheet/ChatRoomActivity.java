package com.akpdeveloper.baatcheet;

import static com.akpdeveloper.baatcheet.SplashActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.isMessageExistInMessageTable;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.isPrimaryKeyExistInMessageTable;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.randomUniqueID;
import static com.akpdeveloper.baatcheet.utilities.DateUtils.getDateFromTimestamp;
import static com.akpdeveloper.baatcheet.utilities.DateUtils.getLongFromTimestamp;
import static com.akpdeveloper.baatcheet.utilities.DateUtils.getTimestampFromLong;
import static com.akpdeveloper.baatcheet.utilities.DateUtils.isNewDay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Range;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.akpdeveloper.baatcheet.adapter.ChatRoomAdapter;
import com.akpdeveloper.baatcheet.databases.MessageTable;
import com.akpdeveloper.baatcheet.enums.MessageStatus;
import com.akpdeveloper.baatcheet.enums.MessageType;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.MessageModel;
import com.akpdeveloper.baatcheet.models.ChatModel;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.utilities.AccessToken;
import com.akpdeveloper.baatcheet.utilities.AndroidUtils;
import com.akpdeveloper.baatcheet.utilities.DateUtils;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.akpdeveloper.baatcheet.databinding.ActivityChatRoomBinding;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChatRoomActivity extends AppCompatActivity {

    private String chatID;
    private final String myAuthUID = FireBaseClass.myUserUID();
    private ActivityChatRoomBinding binding;
    private UserModel user;
    private ChatModel chatModel;
    List<MessageModel> messageModels;
    ChatRoomAdapter chAdapter;
    ValueEventListener chatValueEventListen;
    private String myName;

    public String currentUserInChatRoomActivity(){return user.getuID();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //getting friend user from intent
        user = AndroidUtils.getUserModelFromIntent(getIntent());

        logcat("me UID:"+myAuthUID+" user UID:"+user.getuID());

        //initializing views
        initView();

        //getting chat id for the chat
        chatID = AndroidUtils.getChatIDForChat(user.getuID(), myAuthUID);

        //getting my name from firebase users (firestore)
        FireBaseClass.allUsersCollectionReference().whereEqualTo("uID",FireBaseClass.myUserUID()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<UserModel> um = task.getResult().toObjects(UserModel.class);
                myName = um.get(0).getName();
            }
        });

        //setting value event listener for recycler view
        setValueEventListener();
        //getting chat data(chatting) from room db
        getOrSetChatRoom();
        //setting chat recycler view
        setAdapter();
    }

    private void initView(){
        //setting friend name on top of activity
        binding.ChatActUserName.setText(user.getName());

        //setting friend image on top of activity
        if(user.getImageUrl()!=null) {
            Picasso.get().load(user.getImageUrl()).into(binding.ChatActProfilePicture);
        }else{
            binding.ChatActProfilePicture.setImageResource(R.drawable.baseline_account_circle_24);
        }

        //setting back button to arrow on top of activity
        binding.ChatActBackButton.setOnClickListener(view -> onBackPressed());

        //sending the message after clicking send
        binding.ChatActSendButton.setOnClickListener(view -> {
            sendMessage();
        });
    }

    private void setValueEventListener(){
        //setting chat event listener from firebase realtime db
        chatValueEventListen = new ValueEventListener() {

            //getting call each time when firebase realtime db gets updated
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //flag to check if data snapshot contain any message received by me
                boolean isMessageReceived=false;
                //getting each child
                for(DataSnapshot ds:snapshot.getChildren()){
                    logcat("get data snapshots "+ds.getValue()+" key "+ds.getKey());
                    //if message was send by me
                    if(Objects.equals(ds.child("senderID").getValue(String.class), myAuthUID)){

                        logcat("inside if");

                        //getting index of my message from message model list
                        int index = getIndexOfMessageFromMessageModel(ds.child("messageID").getValue(String.class));
                        //checking if index is valid or not
                        if(index<0 || index>=messageModels.size()) continue;
                        //setting message status in message model list from firebase
                        switch (Objects.requireNonNull(ds.child("messageStatus").getValue(MessageStatus.class))){
                            case SEND_TO_FIREBASE:  messageModels.get(index).setMessageStatus(MessageStatus.SEND_TO_FIREBASE);
                                                    //updating the recycler view
                                                    chAdapter.notifyItemChanged(index);
                                                    //set value in room db
                                                    DB.MessageTableDao().setTheMessageStatus(messageModels.get(index).getMessageID(),MessageStatus.SEND_TO_FIREBASE.ordinal());
                                                    break;
                            case SEND_TO_USER:      messageModels.get(index).setMessageStatus(MessageStatus.SEND_TO_USER);
                                                    //updating the recycler view
                                                    chAdapter.notifyItemChanged(index);
                                                    //set value in room db
                                                    DB.MessageTableDao().setTheMessageStatus(messageModels.get(index).getMessageID(),MessageStatus.SEND_TO_USER.ordinal());
                                                    break;
                            case READ_BY_USER:      messageModels.get(index).setMessageStatus(MessageStatus.READ_BY_USER);
                                                    //updating the recycler view
                                                    chAdapter.notifyItemChanged(index);
                                                    removeMessageFromFirebase(messageModels.get(index).getMessageID());
                                                    //set value in room db
                                                    DB.MessageTableDao().setTheMessageStatus(messageModels.get(index).getMessageID(),MessageStatus.READ_BY_USER.ordinal());
                                                    break;
                        }
                        continue;
                    }
                    isMessageReceived=true;
                    //if message is send by friend user or received by me
                    //extracting message model from firebase
                    MessageModel mm = new MessageModel(
                            ds.child("messageID").getValue(String.class),
                            ds.child("message").getValue(String.class),
                            ds.child("senderID").getValue(String.class),
                            ds.child("type").getValue(MessageType.class),
                            ds.child("messageStatus").getValue(MessageStatus.class),
                            ds.child("timestamp").getValue(DateModel.class)
                    );
                    logcat("onData changes: "+mm );
                    if(isPrimaryKeyExistInMessageTable(mm.getMessageID()) && isMessageExistInMessageTable(mm.getMessage())) continue;
                    MessageTable mt;
                    //check and set if message received is on new date
                    if(messageModels.isEmpty() || isNewDay(messageModels.get(messageModels.size()-1).getTimestamp(),mm.getTimestamp())){
                        //getting random unique id for date message
                        String messageID;
                        do{
                            messageID = randomUniqueID();
                            logcat("me");
                        }while(isPrimaryKeyExistInMessageTable(messageID));
                        //adding date in message model list
                        messageModels.add(new MessageModel(messageID,getDateFromTimestamp(mm.getTimestamp()),mm.getSenderID(),MessageType.DATE,MessageStatus.NO_STATUS,mm.getTimestamp()));
                        //updating the recycler view
                        chAdapter.notifyItemInserted(messageModels.size()-1);
                        //saving date in room db
                        mt = new MessageTable(messageID,user.getuID(),getDateFromTimestamp(mm.getTimestamp()),false,MessageType.DATE.ordinal(),MessageStatus.NO_STATUS.ordinal(),mm.getTimestamp().getSeconds());
                        DB.MessageTableDao().saveMessage(mt);
                    }
                    //adding message in message model
                    messageModels.add(mm);
                    //updating the recycler view
                    chAdapter.notifyItemInserted(messageModels.size()-1);
                    //saving message in room db
                    mt = new MessageTable(mm.getMessageID(),user.getuID(),mm.getMessage(),false,mm.getType().ordinal(),mm.getMessageStatus().ordinal(),mm.getTimestamp().getSeconds());
                    DB.MessageTableDao().saveMessage(mt);
                    logcat("my id: "+myAuthUID+" message sender iD: "+ds.child("senderID").getValue(String.class)+" message ID: "+ds.getKey());
//                    chAdapter.notifyItemInserted(messageModels.size()-1);
//                    removeMessageFromFirebase(ds.getKey());
                    //message seen by me and saved message status on the firebase
                    seenTheMessage(ds.getKey());
                }
                if(isMessageReceived) resetTheFirebaseNewMessage();
                //scroll bottom of the recycler view
                binding.ChatActRecyclerView.scrollToPosition(messageModels.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                logcat("onCancelled: "+error.getMessage());
            }
        };
    }

    private void getOrSetChatRoom() {

        //getting chatroom from firebase realtime db
        FireBaseClass.allChatCollectionReference().child(chatID).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               logcat("task result: "+task.getResult().getValue());
               //getting chat room or model
               chatModel = task.getResult().getValue(ChatModel.class);
               logcat("chatmodel:"+ chatModel);
               if(chatModel==null){
                   //chat room not exist in firebase or first time message
                   //so create new chat room
                   MessageModel messageModel = new MessageModel("101","Want to start the chat",myAuthUID,MessageType.SYSTEM,MessageStatus.NO_STATUS,DateModel.now());
                   chatModel = new ChatModel(chatID, new ArrayList<>(Arrays.asList(myAuthUID, user.getuID())),messageModel);
                   logcat("chatmodel1:"+ chatModel.getChatID()+"::"+chatModel.getUserIDs());
                   logcat(chatModel.toString());
                   try {
                       //setting chat room on firebase realtime db
                       FireBaseClass.allChatCollectionReference().child(chatID).setValue(chatModel).addOnCompleteListener(task1->{
                           if(task1.isSuccessful()){
                               logcat("chatmodel2:"+ chatModel);
                           }
                       });
                   }catch (Exception i){
                       logcat("InvocationException: "+i.getMessage());
                       logcat("cause: "+i.getCause());
                       i.getCause().printStackTrace();
                   }
               }
           }
        });
//        FireBaseClass.getChatDocumentReference(chatID).get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                chatModel = task.getResult().toObject(ChatModel.class);
//                if(chatModel==null){
//                    List<String> person = new ArrayList<>();
//                    person.add(myAuthUID);
//                    person.add(user.getuID());
//                    chatModel = new ChatModel(chatID,person,Timestamp.now(),"",null);
//                    FireBaseClass.getChatDocumentReference(chatID).set(chatModel);
//                }
//            }
//        });
//        FireBaseClass.allChatCollectionReference2().child(user.getuID()).setValue(user);
    }

    private void getMessageFromRoomDB(){
        //getting chats data from room db
        List<MessageTable> messageTables = DB.MessageTableDao().getSomeMessageOfUser(user.getuID(), 0);
        logcat("userid: "+user.getuID());
        logcat("size of message table : "+messageTables.size());
        //initializing message model list
        messageModels = new ArrayList<>();
        //setting message model list from room db
        for(MessageTable i:messageTables){
            String sender;
            if(i.isSendByMe()){
                sender=myAuthUID;
            }else{
                sender= user.getuID();
            }
            messageModels.add(new MessageModel(i.getID(),i.getMessage(),sender,MessageType.values()[i.getType()], MessageStatus.values()[i.getStatus()], getTimestampFromLong(i.getTimestamp())));
        }
        if(!messageModels.isEmpty()){
            logcat("messaage model size: "+messageModels.size());
        }else{
            logcat("no messagemodel");
        }
    }

    private void setAdapter() {
        //getting chat from room db
        getMessageFromRoomDB();

        //setting up recycler view
        chAdapter = new ChatRoomAdapter(this,messageModels,myAuthUID);
        binding.ChatActRecyclerView.setAdapter(chAdapter);
        binding.ChatActRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //scroll bottom of the recycler view
        binding.ChatActRecyclerView.scrollToPosition(messageModels.size()-1);
    }

    private void seenTheMessage(String messageID){
        //update message status on firebase with seen by user
        Map<String,Object> update = new HashMap<>();
        update.put("messageStatus",MessageStatus.READ_BY_USER);
        FireBaseClass.getMessageFromDatabase(chatID).child(messageID).updateChildren(update);
    }

    private void resetTheFirebaseNewMessage() {
        //reset value of newMessageNumber in firebase
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("newMessageNumber",0);
        FireBaseClass.getChatDocumentReference(chatID).updateChildren(childUpdates);
    }

    private void firebaseGetTheMessage(String messageID){
        //update message status on firebase when firebase received message
        Map<String,Object> update = new HashMap<>();
        update.put("messageStatus",MessageStatus.SEND_TO_FIREBASE);
        FireBaseClass.getMessageFromDatabase(chatID).child(messageID).updateChildren(update);
    }

    private void removeMessageFromFirebase(String docID) {
        if(chatModel.getNewMessageNumber()-1>=0)
            chatModel.setNewMessageNumber(chatModel.getNewMessageNumber()-1);
        else
            chatModel.setNewMessageNumber(0);
        FireBaseClass.getMessageFromDatabase(chatID).child(docID).getRef().removeValue();
    }

    private void sendMessage(){
        //getting text(message) from edittext box
        String message = binding.ChatActEditText.getText().toString();

        //generating random unique message id for message
        String messageID,messageID1;
        do{
            messageID = randomUniqueID();
        }while(isPrimaryKeyExistInMessageTable(messageID));
        do{
            messageID1 = randomUniqueID();
        }while(isPrimaryKeyExistInMessageTable(messageID1));

        //check and add new date for the message list
        if(messageModels.isEmpty() || DateUtils.isNewDay(messageModels.get(messageModels.size()-1).getTimestamp(),DateModel.now())){
            //saving the new date message in room db
            MessageTable mt =new MessageTable(messageID1,user.getuID(),getDateFromTimestamp(DateModel.now()),true,MessageType.DATE.ordinal(),MessageStatus.NO_STATUS.ordinal(), Timestamp.now().getSeconds());
            DB.MessageTableDao().saveMessage(mt);
            //adding new date in message model list
            MessageModel messageModel = new MessageModel(messageID1,getDateFromTimestamp(DateModel.now()), myAuthUID, MessageType.DATE,MessageStatus.PENDING, DateModel.now());
            messageModels.add(messageModels.size(),messageModel);
        }
        //creating message model for this new message
        MessageModel messageModel = new MessageModel(messageID,message, myAuthUID, MessageType.TEXT,MessageStatus.PENDING, DateModel.now());


        //saving the message in room db
        MessageTable mt =new MessageTable(messageID,user.getuID(),message,true,MessageType.TEXT.ordinal(),MessageStatus.PENDING.ordinal(), getLongFromTimestamp(DateModel.now()));
        DB.MessageTableDao().saveMessage(mt);
        //adding message in message model list
        messageModels.add(messageModels.size(),messageModel);
        //notify the recycler view
        chAdapter.notifyItemInserted(messageModels.size()-1);

        //updating last message and add 1 to new message number in current chat model and firebase chat room
        chatModel.setLastMessage(messageModel);
        chatModel.setNewMessageNumber(chatModel.getNewMessageNumber()+1);
//        FireBaseClass.getChatDocumentReference(chatID).setValue(chatModel);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("lastMessage",messageModel);
        childUpdates.put("newMessageNumber",chatModel.getNewMessageNumber());
        FireBaseClass.getChatDocumentReference(chatID).updateChildren(childUpdates);

        //sending message to the firebase
//        String messageID = getRandomMessageID();
//        FireBaseClass.getMessageFromDatabase(chatID).child(messageID).get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//
//            }else{
//                logcat("failed");
//            }
//        });

        //adding message in firebase realtime db
        String finalMessageID = messageID;
        FireBaseClass.getMessageFromDatabase(chatID).child(finalMessageID).setValue(messageModel).addOnCompleteListener(task->{
            if (task.isSuccessful()){
                //message is send to the firebase so update the message status
                firebaseGetTheMessage(finalMessageID);
            }
        });

//        FireBaseClass.getMessageFromDatabase(chatID).push().setValue(messageModel).addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                logcat("task result: "+ task);
//            }
//        });

        //empty the edit box
        binding.ChatActEditText.setText("");

        //sending notification to the other user
        notifyPerson(message);

//        FireBaseClass.getMessageCollectionReference(chatID).add(messageModel).addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                binding.ChatActEditText.setText("");
//
//            }
//        });
    }

    private void notifyPerson(String message){
        //return if token is null or empty
        if(user.getToken()==null || user.getToken().isEmpty()) return;
        //creating json object for message sending
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject notificationObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            JSONObject messageObject = new JSONObject();

            notificationObject.put("title",myName);
            notificationObject.put("body", message);

            dataObject.put("userID",FireBaseClass.myUserUID());

            messageObject.put("notification",notificationObject);
            messageObject.put("token",user.getToken());
            messageObject.put("data",dataObject);

            jsonObject.put("message",messageObject);
//            jsonObject.put("data",dataObject);

            //calling backend for message sending
        sendNotification(jsonObject);
        }catch (Exception e){
            logcat("JSON Exception: "+e.getMessage());
        }

    }

    private void sendNotification(@NonNull JSONObject jsonObject){
        logcat("handler for access token start");
        //starting new thread to get the access token for firebase
        new Thread( () -> {
            AccessToken accessToken = new AccessToken();
            //getting access token
            final String token = accessToken.getAccessToken();
            new Handler(Looper.getMainLooper()).post( ()->{
                if(token!=null){
                    //calling the backend api to send message to firebase
                    logcat("Access Token: "+token);
                    callApi(jsonObject,token);
                }else{
                    logcat("Failed to obtain access token");
                }
            });
        }).start();
    }

    private void callApi(@NonNull JSONObject jsonObject,String token){

        //url for my firebase project
        String url = "";

        //backend code
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject, response ->{
            //code run got response
            logcat("volley response : "+response);
        }, volleyError -> {
            //code run got error
            logcat("volley cause: "+volleyError.getCause()+"\nvolley network:"+ volleyError.networkResponse+"\nstatus code: "+volleyError.networkResponse.statusCode+"printtree");
        }) {
            @NonNull
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
//                AccessToken accessToken = new AccessToken();
//                String token = accessToken.getAccessToken();
                logcat("token : "+token);
                Map<String,String> header = new HashMap<>();
                header.put("Content-Type","application/json");
                header.put("Authorization","Bearer "+token);
                return header;
            }
        };

        requestQueue.add(request);

        logcat("json: "+jsonObject);
//
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
//        Request request = new Request.Builder()
//                .url("https://fcm.googleapis.com/v1/projects/messanger-91581/messages:send")
//                .post(requestBody)
//                .header("Authorization","Bearer "+token)
//                .build();
//
//
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                logcat("send Notification failed: "+e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                logcat("notification send");
//            }
//        });


    }

    private int getIndexOfMessageFromMessageModel(String id){
        //getting index of message model from message model list based on message ID
        for(int i=messageModels.size()-1;i>=0;i--){
            if(Objects.equals(messageModels.get(i).getMessageID(), id)){
                return i;
            }
        }
        return -1;
    }
    @Override
    protected void onStart() {
        FireBaseClass.getMessageFromDatabase(chatID).addValueEventListener(chatValueEventListen);
        logcat("onStart ChatRoomActivity");
        super.onStart();
    }
    @Override
    protected void onStop() {
        FireBaseClass.getMessageFromDatabase(chatID).removeEventListener(chatValueEventListen);
        logcat("onStop chatRoomActivity");
        super.onStop();
    }
}