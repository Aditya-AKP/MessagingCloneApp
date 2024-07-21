package com.akpdeveloper.baatcheet.utilities;

import com.akpdeveloper.baatcheet.models.ChatModel;

import java.util.Comparator;

public class ChatModelComparator implements Comparator<ChatModel> {
    @Override
    public int compare(ChatModel c1, ChatModel c2) {
        return (int) (c1.getLastMessage().getTimestamp().getSeconds() - c2.getLastMessage().getTimestamp().getSeconds());
    }
}
