package com.akpdeveloper.baatcheet.databases;


import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {MessageTable.class, ContactTable.class, ChatTable.class},version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageTableDao MessageTableDao();
    public abstract ContactTableDao ContactTableDao();
    public abstract ChatTableDao ChatTableDao();

    public static final Migration MIGRATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE messageTable ADD COLUMN link TEXT DEFAULT NULL");
        }
    };
}

