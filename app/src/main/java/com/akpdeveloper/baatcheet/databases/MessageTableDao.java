package com.akpdeveloper.baatcheet.databases;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMessage(MessageTable messageTable);

    @Query("SELECT * FROM messageTable WHERE friendUID=:userUID ORDER BY timestamp ASC LIMIT 50 OFFSET :number")
    List<MessageTable> getSomeMessageOfUser(String userUID,int number);

    @Query("SELECT COUNT(*) FROM messageTable WHERE ID=:id")
    int numberOfPrimaryKeyExist(String id);

    @Query("SELECT COUNT(*) FROM messageTable WHERE message=:m")
    int numberOfMessages(String m);

    @Query("UPDATE messageTable SET status=:status WHERE ID=:id")
    void setTheMessageStatus(String id,int status);

    @Query("UPDATE messageTable SET link=:url WHERE ID=:id")
    void updateMessageURL(String id,String url);

    @Query("SELECT status FROM messageTable WHERE ID=:id")
    int getTheMessageStatus(String id);

    @Query("DELETE FROM messageTable")
    void deleteAllMessages();
}
