package com.akpdeveloper.baatcheet.adapter;

import static com.akpdeveloper.baatcheet.SplashActivity.logcat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akpdeveloper.baatcheet.enums.MessageFlow;
import com.akpdeveloper.baatcheet.enums.MessageType;
import com.akpdeveloper.baatcheet.models.MessageModel;
import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.utilities.DateUtils;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    List<MessageModel> messageModels;

    //my user iD
    String mCurrentUser;
    Context context;

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public ChatRoomAdapter(Context context,List<MessageModel> messageModels,String currentUser) {
        this.context = context;
        this.messageModels = messageModels;
        this.mCurrentUser = currentUser;
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        TextView messageText;
        TextView timeText;
        ImageView messageStatus;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            //getting element fro the layout
            messageText = itemView.findViewById(R.id.MessageTextView);
            timeText = itemView.findViewById(R.id.MessageTime);
            messageStatus = itemView.findViewById(R.id.MessageStatus);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //getting item view type based on sender, receiver, date
        if(messageModels.get(position).getType()!=MessageType.SYSTEM && messageModels.get(position).getType()!=MessageType.DATE && messageModels.get(position).getType()!=MessageType.UNSUPPORTED) {
            if (messageModels.get(position).getSenderID().equals(mCurrentUser)) {
                return MessageFlow.SEND.ordinal();
            } else {
                return MessageFlow.RECEIVED.ordinal();
            }
        } else if (messageModels.get(position).getType()==MessageType.SYSTEM || messageModels.get(position).getType()==MessageType.DATE) {
            return MessageFlow.SYSTEM.ordinal();
        }else {
            return MessageFlow.UNSUPPORTED.ordinal();
        }
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //setting layout based on send, receive and system
        switch (MessageFlow.values()[viewType]){
            case SEND: view = LayoutInflater.from(context).inflate(R.layout.sender_message_layout,parent,false); break;
            case RECEIVED: view = LayoutInflater.from(context).inflate(R.layout.receiver_message_layout,parent,false); break;
            case SYSTEM: view = LayoutInflater.from(context).inflate(R.layout.system_message_layout,parent,false); break;
            default: view = LayoutInflater.from(context).inflate(R.layout.unsupported_message_layout,parent,false); break;
        }
//        logcat(MessageFlow.values()[viewType]+"");
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        //getting message from list
        MessageModel mm = messageModels.get(position);
//        logcat("on bind: "+mm.getMessage()+"|"+mm.getType());
        //setting layout according to its message type
        switch (mm.getType()){
            case TEXT:  holder.messageText.setText(mm.getMessage());
                        holder.timeText.setText(DateUtils.getTimeFromTimestamp(mm.getTimestamp()));
                        break;
            case IMAGE: break;
            case DATE:
            case SYSTEM:holder.messageText.setText(mm.getMessage());
                        break;
            default:
        }
        //setting layout according to its message status
        if(holder.messageStatus!=null){
            switch (mm.getMessageStatus()){
                case PENDING:           holder.messageStatus.setImageResource(R.drawable.baseline_access_time_24);break;
                case SEND_TO_FIREBASE:  holder.messageStatus.setImageResource(R.drawable.baseline_check_24);break;
                case SEND_TO_USER:      holder.messageStatus.setImageResource(R.drawable.baseline_check_circle_outline_24);break;
                case READ_BY_USER:      holder.messageStatus.setImageResource(R.drawable.baseline_check_circle_24);break;
                default:holder.messageStatus.setVisibility(View.GONE);
            }
        }
    }
}
