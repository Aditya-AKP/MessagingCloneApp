package com.akpdeveloper.baatcheet.fragments.settingsfragments;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.akpdeveloper.baatcheet.R;
import com.squareup.picasso.Picasso;

public class ProfilePicPreference extends Preference {

    String name,about,imgUrl;
    private View.OnClickListener onClickListener;

    public ProfilePicPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        ImageView imageView;
        TextView nameText,aboutText;


        imageView = (ImageView) holder.findViewById(R.id.SettingsPreferenceProfilePic);
        nameText = (TextView) holder.findViewById(R.id.SettingsPreferenceName);
        aboutText = (TextView) holder.findViewById(R.id.SettingsPreferenceAbout);

        nameText.setText(name);
        aboutText.setText(about);
        if(imgUrl!=null) {
            Picasso.get().load(imgUrl).into(imageView);
        }else {
            imageView.setImageResource(R.drawable.baseline_account_circle_24);
        }
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setProfilePicPreference(String name,String about,String imgUrl,View.OnClickListener onclick){
        this.name=name;
        this.about=about;
        this.imgUrl=imgUrl;
        onClickListener=onclick;
    }

}
