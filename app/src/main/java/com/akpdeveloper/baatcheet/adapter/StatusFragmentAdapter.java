package com.akpdeveloper.baatcheet.adapter;

import static com.akpdeveloper.baatcheet.SplashActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.StatusActivity;
import com.akpdeveloper.baatcheet.databases.ContactTable;
import com.akpdeveloper.baatcheet.models.DateModel;
import com.akpdeveloper.baatcheet.models.StatusModel;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.utilities.DateUtils;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class StatusFragmentAdapter extends RecyclerView.Adapter<StatusFragmentAdapter.StatusFragmentViewModel> {

    private Context context;
    private List<DateModel> statusDate;
    private List<String> senderIDList;
    private List<Integer> noOfStatus;

    public StatusFragmentAdapter(Context context, List<DateModel> statusDate,List<String> senderIDList,List<Integer> noOfStatus){
        this.context = context;
        this.statusDate = statusDate;
        this.senderIDList = senderIDList;
        this.noOfStatus = noOfStatus;
    }

    @Override
    public int getItemCount() {return senderIDList.size();}

    @NonNull
    @Override
    public StatusFragmentViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_layout,parent,false);
        return new StatusFragmentViewModel(view);
    }

    public static class StatusFragmentViewModel extends RecyclerView.ViewHolder{

        MaterialTextView nameTextView;
        MaterialTextView descriptionTextView;
        MaterialTextView timeTextView;
        MaterialTextView alertTextView;
        ShapeableImageView imageView;

        public StatusFragmentViewModel(View itemView){
            super(itemView);
            logcat("status fragment view model");
            nameTextView = itemView.findViewById(R.id.user_list_name);
            descriptionTextView = itemView.findViewById(R.id.user_list_description);
            timeTextView = itemView.findViewById(R.id.user_list_time);
            alertTextView = itemView.findViewById(R.id.user_list_alert);
            imageView = itemView.findViewById(R.id.user_list_image);

            alertTextView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull StatusFragmentViewModel holder, int position) {

        FireBaseClass.allUsersCollectionReference().document(senderIDList.get(position)).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel ct = task.getResult().toObject(UserModel.class);
                    holder.nameTextView.setText(ct.getName());

                holder.descriptionTextView.setVisibility(View.GONE);

                holder.timeTextView.setText(DateUtils.getDateFromTimestamp(statusDate.get(position)));

                holder.alertTextView.setText(noOfStatus.get(position).toString());

                if(ct.getImageUrl()==null){
                    holder.imageView.setImageResource(R.drawable.baseline_account_circle_24);
                }else {
                    Picasso.get().load(ct.getImageUrl()).into(holder.imageView);
                }

                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(context, StatusActivity.class);
                    intent.putExtra("senderid",senderIDList.get(position));
                    context.startActivity(intent);
                });
            }
        });


    }
}
