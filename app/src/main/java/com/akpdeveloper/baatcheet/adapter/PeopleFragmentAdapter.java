package com.akpdeveloper.baatcheet.adapter;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.FireBaseClass.myUserUID;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akpdeveloper.baatcheet.ChatRoomActivity;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.utilities.AndroidUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class PeopleFragmentAdapter extends RecyclerView.Adapter<PeopleFragmentAdapter.PeopleFragmentViewHolder> {

    Context context;
    List<UserModel> userModels ;

    @Override
    public int getItemCount(){return userModels.size();}

    public PeopleFragmentAdapter(Context context,List<UserModel> userModels) {
        this.context=context;
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public PeopleFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_layout,parent,false);
        return new PeopleFragmentViewHolder(view);
    }

    public static class PeopleFragmentViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView nameTextView;
        MaterialTextView descriptionTextView;
        MaterialTextView timeTextView;
        MaterialTextView alertTextView;
        ShapeableImageView imageView;
        public PeopleFragmentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.user_list_name);
            descriptionTextView = itemView.findViewById(R.id.user_list_description);
            timeTextView = itemView.findViewById(R.id.user_list_time);
            alertTextView = itemView.findViewById(R.id.user_list_alert);
            imageView = itemView.findViewById(R.id.user_list_image);

            alertTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleFragmentViewHolder holder, int position) {
        UserModel userModel = userModels.get(position);
        logcat(userModel.getName()+userModel.getuID());
        if(Objects.equals(userModel.getuID(), myUserUID())) {
            String str = userModel.getName() + " (ME)";
            holder.nameTextView.setText(str);
        } else holder.nameTextView.setText(userModel.getName());
        holder.descriptionTextView.setText(userModel.getAbout());
        holder.timeTextView.setText(userModel.getNumber());

        if(userModel.getImageUrl()!=null) {
            Picasso.get().load(userModel.getImageUrl()).into(holder.imageView);
        }else{
            holder.imageView.setImageResource(R.drawable.baseline_account_circle_24);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatRoomActivity.class);
            AndroidUtils.setUserModelToIntent(intent,userModel);
            context.startActivity(intent);
        });
    }
}
