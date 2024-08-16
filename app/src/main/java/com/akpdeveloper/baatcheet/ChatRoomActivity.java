package com.akpdeveloper.baatcheet;

import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_NUMBER;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_URL;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MULTIPLE_MEDIA;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_SINGLE_MEDIA;
import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.StartActivity.makeToast;
import static com.akpdeveloper.baatcheet.receiver.DownloadReceiver.pendingDownloadMedia;
import static com.akpdeveloper.baatcheet.services.FCMService.FIREBASE_BODY;
import static com.akpdeveloper.baatcheet.services.FCMService.FIREBASE_TITLE;
import static com.akpdeveloper.baatcheet.services.FCMService.FIREBASE_USER_ID;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.isPrimaryKeyExistInMessageTable;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.messageStatusForMessageInMessageTable;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.randomUniqueID;
import static com.akpdeveloper.baatcheet.utilities.DateUtils.getDateFromTimestamp;
import static com.akpdeveloper.baatcheet.utilities.DateUtils.getLongFromTimestamp;
import static com.akpdeveloper.baatcheet.utilities.DateUtils.getTimestampFromLong;
import static com.akpdeveloper.baatcheet.utilities.DateUtils.isNewDay;
import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.akpdeveloper.baatcheet.utilities.UploadAndDownloadFile;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ChatRoomActivity extends AppCompatActivity {



    private String chatID;
    private final String myAuthUID = FireBaseClass.myUserUID();
    private ActivityChatRoomBinding binding;
    private UserModel user;
    private ChatModel chatModel;
    private String mediaLink;
    private MessageType mMessageType;
    List<MessageModel> messageModels;
    ChatRoomAdapter chAdapter;
    ValueEventListener chatValueEventListen;
    ActivityResultLauncher<Intent> mediaActivityLauncher;
    private String myName;

    private Context activityContext(){return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //getting friend user from intent
        user = AndroidUtils.getUserModelFromIntent(getIntent());

        mediaActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),result->{
                    if(result.getResultCode()==RESULT_OK){
                        if(result.getData()!=null && result.getData().getData()!=null){
                            Uri uri = result.getData().getData();
                            ContentResolver cr = this.getContentResolver();
                            String mimeType = cr.getType(uri);
                            assert mimeType != null;
                            if(mimeType.contains("image")){
                                Glide.with(ChatRoomActivity.this)
                                        .load(uri)
                                        .override(SIZE_ORIGINAL)
                                        .placeholder(R.drawable.progress_animation)
                                        .error(R.drawable.baseline_error_24)
                                        .into(binding.ChatActImageView);
                                mMessageType = MessageType.IMAGE;
                                binding.ChatActCardView.setVisibility(View.VISIBLE);
                                mediaLink = uri.toString();
                            } else if (mimeType.contains("video")) {
                                Glide.with(this)
                                        .load(uri)
                                        .placeholder(R.drawable.progress_animation)
                                        .apply(new RequestOptions())
                                        .thumbnail(Glide.with(this).load(uri))
                                        .error(R.drawable.baseline_error_24)
                                        .into(binding.ChatActImageView);
                                mMessageType = MessageType.VIDEO;
                                binding.ChatActCardView.setVisibility(View.VISIBLE);
                                mediaLink = uri.toString();
                            }

                        }
                    }
                }
        );

        logcat("me UID:"+myAuthUID+" user UID:"+user.getuID());

        resetAfterSendingMessage();

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

        if(MainActivity.isMediaPermissionGranted){
        //setting value event listener for recycler view
        setValueEventListener();
        //getting chat data(chatting) from room db
        getOrSetChatRoom();
        }
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

        binding.ChatActEditText.setKeyBoardInputCallbackListener((inputContentInfo, flags, opts) -> {


            logcat("DESCRIPTION: "+inputContentInfo.getDescription());
            logcat("LABEL: "+inputContentInfo.getDescription().getLabel());
            logcat("MIME TYPE COUNT: "+inputContentInfo.getDescription().getMimeTypeCount());
            logcat("LABEL: "+inputContentInfo.getDescription().describeContents());

            Uri uri ;

            if(inputContentInfo.getDescription().toString().contains("image/gif")) {
                uri = inputContentInfo.getLinkUri();
                Glide.with(ChatRoomActivity.this).asGif().override(SIZE_ORIGINAL).load(uri).into(binding.ChatActImageView);
                mMessageType = MessageType.GIF;
            }else {
                uri = inputContentInfo.getContentUri();
                Glide.with(ChatRoomActivity.this).load(uri).override(SIZE_ORIGINAL).into(binding.ChatActImageView);
                mMessageType = MessageType.STICKER;
            }
            binding.ChatActCardView.setVisibility(View.VISIBLE);
            mediaLink = uri.toString();
        });

        binding.ChatActAttachFile.setOnClickListener(view -> {
            if(MainActivity.isMediaPermissionGranted){
                CharSequence[] item = {"Images","Videos"};

                new AlertDialog.Builder(this)
                        .setTitle("Choose Media to Send")
                        .setItems(item,((dialogInterface, i) -> {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            if(i==0){
                                intent.setType("image/*");
                            } else if (i==1) {
                                intent.setType("video/*");
                            }
                            mediaActivityLauncher.launch(intent);
                        }))
                        .setCancelable(true)
                        .create()
                        .show();
            }else{
                makeToast(this,"Allow Media Permission from Settings");
            }
        });

        binding.ChatActCloseImageView.setOnClickListener(view -> {
            resetAfterSendingMessage();
        });

        //setting back button to arrow on top of activity
        binding.ChatActBackButton.setOnClickListener(view -> onBackPressed());

            //sending the message after clicking send
            binding.ChatActSendButton.setOnClickListener(view -> {
                if(MainActivity.isMediaPermissionGranted) {
                    sendMessage();
                }else{
                    makeToast(this,"Allow Media Permission to send messages");
                }
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
                    if(ds.child("senderID").getValue(String.class)==null) {
                        removeMessageFromFirebase(ds.getKey());
                        continue;
                    }
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
                    if(ds.child("messageStatus").getValue(MessageStatus.class)!=MessageStatus.SEND_TO_FIREBASE) continue;
                    isMessageReceived=true;
                    //if message is send by friend user or received by me
                    //extracting message model from firebase
                    MessageModel mm = new MessageModel(
                            ds.child("messageID").getValue(String.class),
                            ds.child("message").getValue(String.class),
                            ds.child("link").getValue(String.class),
                            ds.child("senderID").getValue(String.class),
                            ds.child("type").getValue(MessageType.class),
                            ds.child("messageStatus").getValue(MessageStatus.class),
                            ds.child("timestamp").getValue(DateModel.class),
                            ds.child("mimeType").getValue(String.class)
                    );
                    logcat("onData changes: "+mm.display() );
                    if(isPrimaryKeyExistInMessageTable(mm.getMessageID()) && messageStatusForMessageInMessageTable(mm.getMessageID())!=MessageStatus.READ_BY_USER) {
                        seenTheMessage(ds.getKey());
                        continue;
                    }
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
                        if(!messageModels.contains(new MessageModel(messageID,getDateFromTimestamp(mm.getTimestamp()),null,mm.getSenderID(),MessageType.DATE,MessageStatus.NO_STATUS,mm.getTimestamp(),mm.getMimeType()))) {
                            messageModels.add(new MessageModel(messageID, getDateFromTimestamp(mm.getTimestamp()),null, mm.getSenderID(), MessageType.DATE, MessageStatus.NO_STATUS, mm.getTimestamp(),mm.getMimeType()));
                            //updating the recycler view
                            chAdapter.notifyItemInserted(messageModels.size()-1);
                        }
                        //saving date in room db
                        mt = new MessageTable(messageID,user.getuID(),getDateFromTimestamp(mm.getTimestamp()),null,false,MessageType.DATE.ordinal(),MessageStatus.NO_STATUS.ordinal(),mm.getTimestamp().getSeconds());
                        DB.MessageTableDao().saveMessage(mt);
                    }


                    //adding message in message model
                    if(!messageModels.contains(mm)) {
                        switch (mm.getType()){
                            case IMAGE:
                                messageModels.add(mm);
                                pendingDownloadMedia.put(mm.getLink(),mm.getMessageID());
                                new UploadAndDownloadFile(activityContext(), mm.getLink(),MessageType.IMAGE,mm.getMimeType()){
                                    @Override
                                    public void failure(Context context, Exception e) {
                                        makeToast(context,"Failed to Download Image");
                                    }
                                }.downloadMedia();
                                break;
                            case VIDEO:
                                messageModels.add(mm);
                                pendingDownloadMedia.put(mm.getLink(), mm.getMessageID());
                                new UploadAndDownloadFile(activityContext(),mm.getLink(),MessageType.VIDEO, mm.getMimeType()){
                                    @Override
                                    public void failure(Context context, Exception e) {
                                        super.failure(context, e);
                                        makeToast(context,"Failed to Download Video");
                                    }
                                }.downloadMedia();
                            case STICKER:
                                messageModels.add(mm);
                                pendingDownloadMedia.put(mm.getLink(),mm.getMessageID());
                                new UploadAndDownloadFile(activityContext(),mm.getLink(),MessageType.STICKER,mm.getMimeType()){
                                    @Override
                                    public void failure(Context context, Exception e) {
                                        super.failure(context, e);
                                        makeToast(context,"Failed to Download Sticker");
                                    }
                                }.downloadMedia();
                            default:
                                messageModels.add(mm);
                        }
                        //updating the recycler view
                        chAdapter.notifyItemInserted(messageModels.size()-1);
                        //saving message in room db
                        mt = new MessageTable(mm.getMessageID(),user.getuID(),mm.getMessage(),mm.getLink()==null?null: mm.getLink(),false,mm.getType().ordinal(),MessageStatus.READ_BY_USER.ordinal(), mm.getTimestamp().getSeconds());
                        DB.MessageTableDao().saveMessage(mt);
                        logcat("my id: "+myAuthUID+" message sender iD: "+ds.child("senderID").getValue(String.class)+" message ID: "+ds.getKey());

                    }
//                    chAdapter.notifyItemInserted(messageModels.size()-1);
//                    removeMessageFromFirebase(ds.getKey());
                    //message seen by me and saved message status on the firebase
                    seenTheMessage(ds.getKey());
                }
                if(isMessageReceived) resetTheFirebaseNewMessage();
                //scroll bottom of the recycler view
//                binding.ChatActRecyclerView.scrollToPosition(messageModels.size()-1);
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
                   MessageModel messageModel = new MessageModel("101","Want to start the chat",null,myAuthUID,MessageType.SYSTEM,MessageStatus.NO_STATUS,DateModel.now(),null);
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
            if(i.isSendByMe() && MessageStatus.values()[i.getStatus()]==MessageStatus.PENDING){
                String finalMessageID = i.getID();
                FireBaseClass.getMessageFromDatabase(chatID).child(finalMessageID)
                .setValue(
                        new MessageModel(
                                i.getID(),
                                i.getMessage(),
                                i.getLink(),
                                sender,
                                MessageType.values()[i.getType()],
                                MessageStatus.values()[i.getStatus()],
                                getTimestampFromLong(i.getTimestamp()),
                                null
                        )
                ).addOnCompleteListener(task->{
                    if (task.isSuccessful()){
                        //message is send to the firebase so update the message status
                        firebaseGetTheMessage(finalMessageID);
                    }
                });
            }
            if(!messageModels.contains(new MessageModel(i.getID(),i.getMessage(),i.getLink(),sender,MessageType.values()[i.getType()], MessageStatus.values()[i.getStatus()], getTimestampFromLong(i.getTimestamp()),null)))
                messageModels.add(new MessageModel(i.getID(),i.getMessage(),i.getLink(),sender,MessageType.values()[i.getType()], MessageStatus.values()[i.getStatus()], getTimestampFromLong(i.getTimestamp()),null));
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
        FireBaseClass.getMessageFromDatabase(chatID).child(messageID).get().addOnCompleteListener(task->{
            if(task.isSuccessful()){
                if(task.getResult().exists()){
                    FireBaseClass.getMessageFromDatabase(chatID).child(messageID).updateChildren(update);
                }
            }
        });
    }

    private void resetTheFirebaseNewMessage() {
        //reset value of newMessageNumber in firebase
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("newMessageNumber",0);
        chatModel.setNewMessageNumber(0);
        FireBaseClass.getChatDocumentReference(chatID).updateChildren(childUpdates);
    }

    private void firebaseGetTheMessage(String messageID){
        //update message status on firebase when firebase received message
        Map<String,Object> update = new HashMap<>();
        update.put("messageStatus",MessageStatus.SEND_TO_FIREBASE);
        FireBaseClass.getMessageFromDatabase(chatID).child(messageID).updateChildren(update);
    }

    private void removeMessageFromFirebase(String docID) {
//        if(chatModel.getNewMessageNumber()-1>=0)
//            chatModel.setNewMessageNumber(chatModel.getNewMessageNumber()-1);
//        else
            chatModel.setNewMessageNumber(0);
        FireBaseClass.getMessageFromDatabase(chatID).child(docID).get().addOnCompleteListener(task->{
            if(task.isSuccessful()){
                    if (task.getResult().getValue(MessageModel.class).getMessageStatus() == MessageStatus.READ_BY_USER) {
//                        if (task.getResult().getValue(MessageModel.class).getLink() != null && task.getResult().getValue(MessageModel.class).getType()!=MessageType.GIF) {
//                            FirebaseStorage.getInstance().getReferenceFromUrl(task.getResult().getValue(MessageModel.class).getLink()).delete();
//                        }
                        FireBaseClass.getMessageFromDatabase(chatID).child(docID).getRef().removeValue();
                    }
            }
        });
    }

    private void sendMessage(){
        String message = binding.ChatActEditText.getText().toString();
        switch (mMessageType){
            case TEXT://getting text(message) from edittext box
                if(message.isEmpty()){return;}else{saveSendMessage(message,null,mediaLink);}
                break;
            case IMAGE:
            case VIDEO:
            case FILE:
            case STICKER:
                new UploadAndDownloadFile(this, mediaLink,mMessageType){

                    @Override
                    public void uploadCompleted(Context context, String url, String mime) {
                        super.uploadCompleted(context, url, mime);
                        String old = mediaLink;
                        mediaLink =url;
                        saveSendMessage(message,mime,old);
                    }

                    @Override
                    public void failure(Context context, Exception e) {
                        logcat("ChatRoomActivity: Failed To Upload e: "+e.getMessage());
                        makeToast(context,"Upload Failed");
                    }

                    @Override
                    public void preConnection(Context context) {
                        startProgress(true);
                    }

                    @Override
                    public void postConnection(Context context) {
                        startProgress(false);
                    }
                }.uploadMedia();
                break;
            case GIF:
            default:saveSendMessage(message,null,mediaLink);
        }

    }

    private void saveSendMessage(String message,String mime,String oldUrl){
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
            MessageTable mt =new MessageTable(messageID1,user.getuID(),getDateFromTimestamp(DateModel.now()),null,true,MessageType.DATE.ordinal(),MessageStatus.NO_STATUS.ordinal(), Timestamp.now().getSeconds());
            DB.MessageTableDao().saveMessage(mt);
            //adding new date in message model list
            MessageModel messageModel = new MessageModel(messageID1,getDateFromTimestamp(DateModel.now()),null, myAuthUID, MessageType.DATE,MessageStatus.PENDING, DateModel.now(),null);
            if(!messageModels.contains(messageModel))
                messageModels.add(messageModels.size(),messageModel);
        }
        //creating message model for this new message
        MessageModel messageModel = new MessageModel(messageID,message,mediaLink, myAuthUID, mMessageType,MessageStatus.PENDING, DateModel.now(),mime);

        MessageModel localMessageModel;
        localMessageModel = messageModel;
        logcat("localmM: "+localMessageModel.display());
        localMessageModel.setLink(oldUrl);
        logcat("localMM: "+localMessageModel.display());

        //saving the message in room db
        MessageTable mt =new MessageTable(messageID,user.getuID(),message,oldUrl,true,mMessageType.ordinal(),MessageStatus.PENDING.ordinal(), getLongFromTimestamp(DateModel.now()));
        DB.MessageTableDao().saveMessage(mt);
        //adding message in message model list
        if(!messageModels.contains(localMessageModel))
            messageModels.add(messageModels.size(),localMessageModel);
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



        resetAfterSendingMessage();
        //empty the edit box
        binding.ChatActEditText.setText("");

        binding.ChatActRecyclerView.scrollToPosition(messageModels.size()-1);

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
            JSONObject dataObject = new JSONObject();
            JSONObject messageObject = new JSONObject();



            dataObject.put(FIREBASE_USER_ID,FireBaseClass.myUserUID());
            dataObject.put(FIREBASE_TITLE,myName);
            dataObject.put(FIREBASE_BODY, message);


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
//                .url("")
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

    private void startProgress(boolean isProgress){
        if(isProgress){
            binding.ChatActProgressBar.setVisibility(View.VISIBLE);
        }else{
            binding.ChatActProgressBar.setVisibility(View.GONE);
        }
    }

    private void resetAfterSendingMessage(){
        binding.ChatActCardView.setVisibility(View.GONE);
        mMessageType = MessageType.TEXT;
        mediaLink = null;
    }

    private void sendIntentMedia(){
        if(getIntent().hasExtra(MY_MEDIA_NUMBER)){
            if(Objects.equals(getIntent().getStringExtra(MY_MEDIA_NUMBER), MY_SINGLE_MEDIA)){
                if (getIntent().getParcelableExtra(MY_MEDIA_URL)!=null) {
                    String uri = getIntent().getParcelableExtra(MY_MEDIA_URL).toString();
                    String mime = getContentResolver().getType(Uri.parse(uri));
                    if(mime==null)return;
                    if(mime.contains("image")){
                        mMessageType = MessageType.IMAGE;
                    } else if (mime.contains("video")) {
                        mMessageType = MessageType.VIDEO;
                    }
                    mediaLink = uri;
                }
                sendMessage();
            } else if (Objects.equals(getIntent().getStringExtra(MY_MEDIA_NUMBER), MY_MULTIPLE_MEDIA)) {
                makeToast(this,"Can't support sending multiple media now");
            }
        }
    }

    @Override
    protected void onStart() {
        FireBaseClass.getMessageFromDatabase(chatID).addValueEventListener(chatValueEventListen);
        sendIntentMedia();
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