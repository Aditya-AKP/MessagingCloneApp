package com.akpdeveloper.baatcheet.databases;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MessageTable.class, ContactTable.class, ChatTable.class},version = 6)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageTableDao MessageTableDao();
    public abstract ContactTableDao ContactTableDao();
    public abstract ChatTableDao ChatTableDao();
}
