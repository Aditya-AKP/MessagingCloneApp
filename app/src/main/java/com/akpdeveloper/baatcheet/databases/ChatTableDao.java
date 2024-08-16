package com.akpdeveloper.baatcheet.databases;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatTableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveChat(ChatTable chatTable);

    @Query("SELECT * FROM chatTable")
    List<ChatTable> getAllChats();

    @Query("UPDATE chatTable SET newMessageNumber=:newMessageNumber WHERE chatID=:chatId")
    void updateTheNewMessageNumber(String chatId,int newMessageNumber);
}
