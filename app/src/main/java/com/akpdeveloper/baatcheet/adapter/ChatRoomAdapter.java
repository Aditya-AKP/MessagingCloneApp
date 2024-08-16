package com.akpdeveloper.baatcheet.adapter;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.core.content.ContextCompat.getSystemService;

import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_NUMBER;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_MEDIA_URL;
import static com.akpdeveloper.baatcheet.MediaViewActivity.MY_SINGLE_MEDIA;
import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.StartActivity.makeToast;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akpdeveloper.baatcheet.MediaViewActivity;
import com.akpdeveloper.baatcheet.enums.MessageFlow;
import com.akpdeveloper.baatcheet.enums.MessageType;
import com.akpdeveloper.baatcheet.models.MessageModel;
import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.utilities.DateUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
        ImageView messageImage;
        TextView messageText;
        TextView timeText;
        ImageView messageStatus;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            //getting element from the layout
            messageImage = itemView.findViewById(R.id.MessageImageView);
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
        closeAll(holder);
        //getting message from list
        MessageModel mm = messageModels.get(position);
//        logcat("on bind: "+mm.getMessage()+"|"+mm.getType());
        //setting layout according to its message type
        switch (mm.getType()){
            case TEXT:
                holder.messageText.setVisibility(View.VISIBLE);
                holder.timeText.setText(DateUtils.getTimeFromTimestamp(mm.getTimestamp()));
                break;
            case IMAGE:
                holder.messageImage.setVisibility(View.VISIBLE);
                holder.timeText.setText(DateUtils.getTimeFromTimestamp(mm.getTimestamp()));
                if(mm.getLink()!=null) {
                    Glide.with(context)
                            .load(mm.getLink())
                            .placeholder(R.drawable.progress_animation)
                            .error(R.drawable.baseline_error_24)
                            .into(holder.messageImage);
                    holder.messageImage.setOnClickListener(view -> {
                        Intent intent = new Intent(context, MediaViewActivity.class);
                        intent.putExtra(MY_MEDIA_URL,mm.getLink());
                        intent.putExtra(MY_MEDIA_NUMBER,MY_SINGLE_MEDIA);
                        context.startActivity(intent);
                    });
                }

                break;
            case GIF:
                holder.messageImage.setVisibility(View.VISIBLE);
                holder.timeText.setText(DateUtils.getTimeFromTimestamp(mm.getTimestamp()));
                if(mm.getLink()!=null) {
                    Glide.with(context)
                            .asGif()
                            .placeholder(R.drawable.progress_animation)
                            .error(R.drawable.baseline_error_24)
                            .load(mm.getLink())
                            .into(holder.messageImage);
                    holder.messageImage.setOnClickListener(view -> {
                        Intent intent = new Intent(context, MediaViewActivity.class);
                        intent.putExtra(MY_MEDIA_URL,mm.getLink());
                        intent.putExtra(MY_MEDIA_NUMBER,MY_SINGLE_MEDIA);
                        context.startActivity(intent);
                    });
                }

                break;
            case STICKER:
                holder.messageImage.setVisibility(View.VISIBLE);
                holder.timeText.setText(DateUtils.getTimeFromTimestamp(mm.getTimestamp()));
                if(mm.getLink()!=null) {
                    Glide.with(context)
                            .asBitmap()
                            .load(mm.getLink())
                            .placeholder(R.drawable.progress_animation)
                            .error(R.drawable.baseline_error_24)
                            .into(holder.messageImage);
                    holder.messageImage.setOnClickListener(view -> {
                        Intent intent = new Intent(context, MediaViewActivity.class);
                        intent.putExtra(MY_MEDIA_URL,mm.getLink());
                        intent.putExtra(MY_MEDIA_NUMBER,MY_SINGLE_MEDIA);
                        context.startActivity(intent);
                    });
                }
                break;
            case VIDEO:
                holder.messageImage.setVisibility(View.VISIBLE);
                holder.timeText.setText(DateUtils.getTimeFromTimestamp(mm.getTimestamp()));
                if(mm.getLink()!=null) {
                    Glide.with(context)
                            .load(mm.getLink())
                            .placeholder(R.drawable.progress_animation)
                            .apply(new RequestOptions())
                            .thumbnail(Glide.with(context).load(mm.getLink()))
                            .error(R.drawable.baseline_error_24)
                            .into(holder.messageImage);
                    holder.messageImage.setOnClickListener(view -> {
                        Intent intent = new Intent(context, MediaViewActivity.class);
                        intent.putExtra(MY_MEDIA_URL,mm.getLink());
                        intent.putExtra(MY_MEDIA_NUMBER,MY_SINGLE_MEDIA);
                        context.startActivity(intent);
                    });
                }

                break;
            case DATE:
            case SYSTEM:
                break;
            default:
        }
        if(!mm.getMessage().isEmpty()){
            holder.messageText.setVisibility(View.VISIBLE);
            holder.messageText.setText(mm.getMessage());
        }


        //setting function for long press on the chat layout
        holder.itemView.setOnLongClickListener(view -> {
            switch (mm.getType()){
                case TEXT:
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Text:",mm.getMessage());
                    clipboardManager.setPrimaryClip(clip);
                    makeToast(context,"Message Copied");
                    break;
                case IMAGE:
                case VIDEO:
                case DATE:
                case SYSTEM:
                case GIF:
                default:
            }
            logcat(mm.display());
            return false;
        });
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

    private void closeAll(ChatRoomViewHolder holder){
        if (holder.messageImage!=null)
            holder.messageImage.setVisibility(View.GONE);
        if (holder.messageText!=null)
            holder.messageText.setVisibility(View.GONE);
    }

}
