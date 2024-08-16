package com.akpdeveloper.baatcheet.adapter;

import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_NUMBER;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_SEND_LOC;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_URL;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MULTIPLE_MEDIA;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_SINGLE_MEDIA;
import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.getOtherUserID;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akpdeveloper.baatcheet.ChatRoomActivity;
import com.akpdeveloper.baatcheet.MediaViewActivity;
import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.databases.ContactTable;
import com.akpdeveloper.baatcheet.models.ChatModel;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.utilities.AndroidUtils;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ShareActivityAdapter extends RecyclerView.Adapter<ShareActivityAdapter.ShareActivityViewModel> {

    Context context;
    ArrayList<ChatModel> chats;
    Intent intent;


    public ShareActivityAdapter(Context context,ArrayList<ChatModel> chats,Intent intent){
        this.context=context;
        this.chats=chats;
        this.intent=intent;
    }


    @Override
    public int getItemCount() {return chats.size();}

    @NonNull
    @Override
    public ShareActivityViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_layout,parent,false);
        return new ShareActivityViewModel(view);
    }


    public static class ShareActivityViewModel extends RecyclerView.ViewHolder{

        MaterialTextView nameTextView;
        MaterialTextView descriptionTextView;
        MaterialTextView timeTextView;
        MaterialTextView alertTextView;
        ShapeableImageView imageView;

        public ShareActivityViewModel(@NonNull View itemView) {
            super(itemView);
            logcat("chat fragment view model");
            nameTextView = itemView.findViewById(R.id.user_list_name);
            descriptionTextView = itemView.findViewById(R.id.user_list_description);
            timeTextView = itemView.findViewById(R.id.user_list_time);
            alertTextView = itemView.findViewById(R.id.user_list_alert);
            imageView = itemView.findViewById(R.id.user_list_image);

            alertTextView.setVisibility(View.GONE);
            timeTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ShareActivityViewModel holder, int position) {

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
            String name = (ct.getCustomName()==null || ct.getCustomName().isEmpty() )?um.getName(): ct.getCustomName();
            holder.nameTextView.setText(name);
            holder.descriptionTextView.setText(um.getAbout());
            if(um.getImageUrl()!=null)
                Picasso.get().load(um.getImageUrl()).into(holder.imageView);
            else
                holder.imageView.setImageResource(R.drawable.baseline_account_circle_24);

            holder.itemView.setOnClickListener(view -> {
                Intent intent1 = new Intent(context, MediaViewActivity.class);
                AndroidUtils.setUserModelToIntent(intent1,um);
                if(Objects.equals(intent.getStringExtra(MY_MEDIA_NUMBER), MY_SINGLE_MEDIA)){
                    intent1.putExtra(MY_MEDIA_URL,(Parcelable) intent.getParcelableExtra(MY_MEDIA_URL));
                    intent1.putExtra(MY_MEDIA_NUMBER,MY_SINGLE_MEDIA);
                } else if (Objects.equals(intent.getStringExtra(MY_MEDIA_NUMBER), MY_MULTIPLE_MEDIA)) {
                    intent1.putExtra(MY_MEDIA_URL,intent.getParcelableArrayExtra(MY_MEDIA_URL));
                    intent1.putExtra(MY_MEDIA_NUMBER,MY_MULTIPLE_MEDIA);
                }
                intent1.putExtra(MY_MEDIA_SEND_LOC,intent.getStringExtra(MY_MEDIA_SEND_LOC));
                view.getContext().startActivity(intent1);
            });
        }else{
            getAndSetUserFromFirebase(holder,position);
        }
    }

    private void getAndSetUserFromFirebase(@NonNull ShareActivityViewModel holder, int position){
        FireBaseClass.getOtherUserByUID(chats.get(position).getUserIDs())
                .get().addOnCompleteListener(task -> {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    assert userModel!=null;
                    //set the name if custom name not present
                    holder.nameTextView.setText(userModel.getName());
                    holder.descriptionTextView.setText(userModel.getAbout());
                    //set the profile picture
                    if(userModel.getImageUrl()==null){
                        holder.imageView.setImageResource(R.drawable.baseline_account_circle_24);
                    }else {
                        Picasso.get().load(userModel.getImageUrl()).into(holder.imageView);
                    }
                    //set the click listener
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent1 = new Intent(context, MediaViewActivity.class);
                        AndroidUtils.setUserModelToIntent(intent1,userModel);
                        if(Objects.equals(intent.getStringExtra(MY_MEDIA_NUMBER), MY_SINGLE_MEDIA)){
                            intent1.putExtra(MY_MEDIA_URL,(Parcelable) intent.getParcelableExtra(MY_MEDIA_URL));
                            intent1.putExtra(MY_MEDIA_NUMBER,MY_SINGLE_MEDIA);
                        } else if (Objects.equals(intent.getStringExtra(MY_MEDIA_NUMBER), MY_MULTIPLE_MEDIA)) {
                            intent1.putExtra(MY_MEDIA_URL,intent.getParcelableArrayExtra(MY_MEDIA_URL));
                            intent1.putExtra(MY_MEDIA_NUMBER,MY_MULTIPLE_MEDIA);
                        }
                        intent1.putExtra(MY_MEDIA_SEND_LOC,intent.getStringExtra(MY_MEDIA_SEND_LOC));
                        view.getContext().startActivity(intent1);
                    });
                });
    }
}
