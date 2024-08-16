package com.akpdeveloper.baatcheet.adapter;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.getOtherUserID;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akpdeveloper.baatcheet.ChatRoomActivity;
import com.akpdeveloper.baatcheet.MediaViewActivity;
import com.akpdeveloper.baatcheet.databases.ContactTable;
import com.akpdeveloper.baatcheet.models.ChatModel;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.utilities.AndroidUtils;
import com.akpdeveloper.baatcheet.utilities.DateUtils;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.ChatFragmentViewModel> {

    Context context;
    List<ChatModel> chats;
    boolean isUpdateFromFirebase = false;

    public ChatFragmentAdapter(Context context,List<ChatModel> chats){
        this.context=context;
        this.chats = chats;
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setUpdateFromFirebase(boolean isUpdateFromFirebase){this.isUpdateFromFirebase=isUpdateFromFirebase;}
    @NonNull
    @Override
    public ChatFragmentViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_layout,parent,false);
        return new ChatFragmentViewModel(view);
    }

    public static class ChatFragmentViewModel extends RecyclerView.ViewHolder {

        MaterialTextView nameTextView;
        MaterialTextView descriptionTextView;
        MaterialTextView timeTextView;
        MaterialTextView alertTextView;
        ShapeableImageView imageView;

        public ChatFragmentViewModel(@NonNull View itemView) {
            super(itemView);
            logcat("chat fragment view model");
            nameTextView = itemView.findViewById(R.id.user_list_name);
            descriptionTextView = itemView.findViewById(R.id.user_list_description);
            timeTextView = itemView.findViewById(R.id.user_list_time);
            alertTextView = itemView.findViewById(R.id.user_list_alert);
            imageView = itemView.findViewById(R.id.user_list_image);

            alertTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatFragmentViewModel holder, int position) {

        //if custom name is not present than use firebase one
//        if(contactUser.getCustomName()==null || contactUser.getCustomName().isEmpty()){
//            String name = DB.ContactTableDao().getName(AndroidUtils.getOtherUserID(chats.get(position).getUserIDs()));
//            holder.nameTextView.setText(name);
//        }else {
//            holder.nameTextView.setText(contactUser.getCustomName());
//        }

        logcat("CHAT: "+chats.get(position).getNewMessageNumber());
        //set the time
        holder.timeTextView.setText(DateUtils.getDateFromTimestamp(chats.get(position).getLastMessage().getTimestamp()));
        //set the description/last message
        StringBuilder name;
        switch(chats.get(position).getLastMessage().getType()){
            case IMAGE:
                if( !chats.get(position).getLastMessage().getMessage().isEmpty()){
                    name = new StringBuilder("IMAGE : "+chats.get(position).getLastMessage().getMessage());
                }else{
                    name = new StringBuilder("IMAGE");
                }
                holder.descriptionTextView.setText(name);
                holder.descriptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_insert_photo_24,0,0,0);
                break;
            case GIF:
                if( !chats.get(position).getLastMessage().getMessage().isEmpty()){
                    name = new StringBuilder("GIF : "+chats.get(position).getLastMessage().getMessage());
                }else{
                    name = new StringBuilder("GIF");
                }
                holder.descriptionTextView.setText(name);
                holder.descriptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_insert_photo_24,0,0,0);
                break;
            case VIDEO:
                if( !chats.get(position).getLastMessage().getMessage().isEmpty()){
                    name = new StringBuilder("VIDEO : "+chats.get(position).getLastMessage().getMessage());
                }else{
                    name = new StringBuilder("VIDEO");
                }
                holder.descriptionTextView.setText(name);
                holder.descriptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_insert_photo_24,0,0,0);
                break;
            case FILE:
                if( !chats.get(position).getLastMessage().getMessage().isEmpty()){
                    name = new StringBuilder("FILE : "+chats.get(position).getLastMessage().getMessage());
                }else{
                    name = new StringBuilder("FILE");
                }
                holder.descriptionTextView.setText(name);
                holder.descriptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_insert_photo_24,0,0,0);
                break;
            case STICKER:
                if( !chats.get(position).getLastMessage().getMessage().isEmpty()){
                    name = new StringBuilder("STICKER : "+chats.get(position).getLastMessage().getMessage());
                }else{
                    name = new StringBuilder("STICKER");
                }
                holder.descriptionTextView.setText(name);
                holder.descriptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_insert_photo_24,0,0,0);
                break;
            default:
                if( !chats.get(position).getLastMessage().getMessage().isEmpty()){
                    name = new StringBuilder(chats.get(position).getLastMessage().getMessage());
                    holder.descriptionTextView.setText(name);
                }
                holder.descriptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
                break;
        }


        //setting the profile picture
//        if(contactUser.getImageUrl()==null){
//            holder.imageView.setImageResource(R.drawable.baseline_account_circle_24);
//        }else {
//            Picasso.get().load(contactUser.getImageUrl()).into(holder.imageView);
//        }

        //setting the new message indicator
        if(chats.get(position).getNewMessageNumber()>0 && Objects.equals(chats.get(position).getLastMessage().getSenderID(), AndroidUtils.getOtherUserID(chats.get(position).getUserIDs()))){
            holder.alertTextView.setVisibility(View.VISIBLE);
            holder.alertTextView.setText(String.valueOf(chats.get(position).getNewMessageNumber()));
        }else {
            holder.alertTextView.setVisibility(View.GONE);
        }


        ContactTable ct = DB.ContactTableDao().getContact(getOtherUserID(chats.get(position).getUserIDs()));
        if(ct!=null){
            UserModel um = new UserModel(
                    ct.getName(),
                    ct.getAbout(),
                    ct.getUID(),
                    ct.getNumber(),
                    ct.getImageUrl(),
                    new DateModel(ct.getAccountCreationTime(),0)
            );
            String name1 = ct.getCustomName()==null?um.getName(): ct.getCustomName();
            holder.nameTextView.setText(name1);
            if(um.getImageUrl()!=null)
                Picasso.get().load(um.getImageUrl()).into(holder.imageView);
            else
                holder.imageView.setImageResource(R.drawable.baseline_account_circle_24);

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, MediaViewActivity.class);
                AndroidUtils.setUserModelToIntent(intent,um);
                view.getContext().startActivity(intent);
            });
        }

        //set the onclick on recycler view element
        //user is passed without token
//        holder.itemView.setOnClickListener(view -> {
//            Intent intent = new Intent(context, ChatRoomActivity.class);
//            UserModel userModel = new UserModel(contactUser.getName(),contactUser.getAbout(),contactUser.getUID(),contactUser.getNumber(),contactUser.getImageUrl(),new DateModel(contactUser.getAccountCreationTime(),0));
//            AndroidUtils.setUserModelToIntent(intent,userModel);
//            view.getContext().startActivity(intent);
//        });
        getAndSetUserFromFirebase(holder,position);

    }

    private void getAndSetUserFromFirebase(@NonNull ChatFragmentViewModel holder, int position){
        FireBaseClass.getOtherUserByUID(chats.get(position).getUserIDs())
                .get().addOnCompleteListener(task -> {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    assert userModel!=null;
                    //set the name if custom name not present
                        holder.nameTextView.setText(userModel.getName());
                    //set the profile picture
                    if(userModel.getImageUrl()==null){
                        holder.imageView.setImageResource(R.drawable.baseline_account_circle_24);
                    }else {
                        Picasso.get().load(userModel.getImageUrl()).into(holder.imageView);
                    }
                    //set the click listener
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(context, ChatRoomActivity.class);
                        AndroidUtils.setUserModelToIntent(intent,userModel);
                        view.getContext().startActivity(intent);
                    });
                });
    }

}
